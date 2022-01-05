package com.ss.camper.store.application;

import com.ss.camper.store.application.dto.StoreDTO;
import com.ss.camper.store.application.dto.StoreTagDTO;
import com.ss.camper.store.application.exception.NotFoundStoreException;
import com.ss.camper.store.application.exception.NotSupplyStoreTypeException;
import com.ss.camper.store.domain.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class StoreServiceV1 implements StoreService {

    private final ModelMapper modelMapper;
    private final StoreRepository storeRepository;
    private final StoreTagRepository storeTagRepository;
    private final StoreRepositorySupport storeRepositorySupport;

    @Override
    @Transactional
    public StoreDTO register(StoreDTO storeDTO) {
        final StoreType storeType = storeDTO.getStoreType();
        Store savedStore;
        switch (storeType) {
            case campGround:
                savedStore = storeRepository.save(CampGroundStore.builder()
                    .storeType(storeDTO.getStoreType())
                    .storeName(storeDTO.getStoreName())
                    .address(storeDTO.getAddress())
                    .tel(storeDTO.getTel())
                    .homepageUrl(storeDTO.getHomepageUrl())
                    .reservationUrl(storeDTO.getReservationUrl())
                    .introduction(storeDTO.getIntroduction())
                    .build());
                break;
            case campSupply:
                savedStore = storeRepository.save(CampSupplyStore.builder()
                    .storeType(storeDTO.getStoreType())
                    .storeName(storeDTO.getStoreName())
                    .address(storeDTO.getAddress())
                    .tel(storeDTO.getTel())
                    .homepageUrl(storeDTO.getHomepageUrl())
                    .reservationUrl(storeDTO.getReservationUrl())
                    .introduction(storeDTO.getIntroduction())
                    .build());
                break;
            default:
                throw new NotSupplyStoreTypeException();
        }

        updateTags(savedStore, storeDTO.getTags());
        return modelMapper.map(savedStore, StoreDTO.class);
    }


    @Override
    @Transactional
    public StoreDTO modify(long storeId, StoreDTO storeDTO) {
        final Store store = storeRepository.findById(storeId)
            .orElseThrow(NotFoundStoreException::new);
        store.updateInfo(
            storeDTO.getStoreName(),
            storeDTO.getAddress(),
            storeDTO.getTel(),
            storeDTO.getHomepageUrl(),
            storeDTO.getReservationUrl(),
            storeDTO.getIntroduction()
        );
        updateTags(store, storeDTO.getTags());
        return modelMapper.map(store, StoreDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public StoreDTO getInfo(long id) {
        final Store store = storeRepository.findById(id).orElse(null);
        return store == null ? null : modelMapper.map(store, StoreDTO.class);
    }

    @Override
    public Page<StoreDTO> getPageList(int size, int page) {
        return storeRepositorySupport.findPageListBySearch(size, page);
    }



    private void updateTags(Store store, Set<StoreTagDTO> tagsDTO) {
        LinkedHashSet<StoreTag> tags = null;
        if (tagsDTO != null && !tagsDTO.isEmpty()) {
            tags = new LinkedHashSet<>();
            for (StoreTagDTO tagDTO : tagsDTO) {
                final Optional<StoreTag> storeTag = storeTagRepository.findByStoreTypeAndTitle(store.getStoreType(), tagDTO.getTitle());
                if (storeTag.isPresent()) {
                    tags.add(storeTag.get());
                } else {
                    tags.add(storeTagRepository.save(StoreTag.builder().storeType(store.getStoreType()).title(tagDTO.getTitle()).build()));
                }
            }
        }
        store.updateTags(tags);
    }

}
