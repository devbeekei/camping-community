package com.ss.camper.store.ui;

import com.ss.camper.common.ControllerTest;
import com.ss.camper.common.WithMockCustomUser;
import com.ss.camper.common.payload.PageDTO;
import com.ss.camper.common.util.JWTUtil;
import com.ss.camper.store.application.StoreProfileImageService;
import com.ss.camper.store.application.StoreService;
import com.ss.camper.store.application.dto.StoreDTO;
import com.ss.camper.store.application.dto.StoreListDTO;
import com.ss.camper.store.domain.StoreType;
import com.ss.camper.store.ui.payload.DeleteStoreProfileImagesPayload;
import com.ss.camper.store.ui.payload.ModifyStorePayload;
import com.ss.camper.store.ui.payload.RegisterStorePayload;
import com.ss.camper.uploadFile.dto.UploadFileDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.ss.camper.common.ApiDocumentAttributes.*;
import static com.ss.camper.common.ApiDocumentUtil.*;
import static com.ss.camper.store.StoreMock.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StoreController.class)
class StoreControllerTest extends ControllerTest {

    @MockBean
    private StoreService storeService;

    @MockBean
    private StoreProfileImageService storeProfileImageService;

    @Test
    @WithMockCustomUser
    void 매장_등록() throws Exception {
        final StoreDTO storeDTO = initStoreDTO(1L, new HashSet<>(){{
            add(initStoreTagDTO(1L, TAG_TITLE1));
            add(initStoreTagDTO(2L, TAG_TITLE2));
        }});
        given(storeService.registerStore(anyLong(), any(StoreDTO.class))).willReturn(storeDTO);

        final RegisterStorePayload.Request request = RegisterStorePayload.Request.builder()
            .storeStatus(STORE_STATUS)
            .storeType(STORE_TYPE)
            .storeName(STORE_NAME)
            .zipCode(ADDRESS.getZipCode())
            .defaultAddress(ADDRESS.getDefaultAddress())
            .detailAddress(ADDRESS.getDetailAddress())
            .latitude(ADDRESS.getLatitude())
            .longitude(ADDRESS.getLongitude())
            .tel(TEL)
            .homepageUrl(HOMEPAGE_URL)
            .reservationUrl(RESERVATION_URL)
            .introduction(INTRODUCTION)
            .openingDays(OPENING_DAYS)
            .openTime(OPEN_TIME)
            .closeTime(CLOSE_TIME)
            .tags(new HashSet<>(){{
                add(TAG_TITLE1);
                add(TAG_TITLE2);
            }})
            .build();

        final ResultActions result = mockMvc.perform(
            post("/store")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON)
                .header(JWTUtil.AUTHORIZATION_HEADER, JWTUtil.BEARER_PREFIX + "{token}")
        );

        result.andExpect(status().isOk())
            .andDo(document("store/register",
                getDocumentRequest(),
                getDocumentResponse(),
                requestFields(
                    fieldWithPath("storeType").type(JsonFieldType.STRING).description("매장 유형").attributes(storeTypeAttribute()),
                    fieldWithPath("storeStatus").type(JsonFieldType.STRING).description("매장 상태").attributes(storeStatusAttribute()),
                    fieldWithPath("storeName").type(JsonFieldType.STRING).description("매장 명"),
                    fieldWithPath("zipCode").type(JsonFieldType.STRING).description("우편 번호"),
                    fieldWithPath("defaultAddress").type(JsonFieldType.STRING).description("기본 주소"),
                    fieldWithPath("detailAddress").type(JsonFieldType.STRING).optional().description("상세 주소"),
                    fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
                    fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("경도"),
                    fieldWithPath("tel").type(JsonFieldType.STRING).description("연락처"),
                    fieldWithPath("homepageUrl").type(JsonFieldType.STRING).optional().description("홈페이지 URL"),
                    fieldWithPath("reservationUrl").type(JsonFieldType.STRING).optional().description("예약 사이트 URL"),
                    fieldWithPath("introduction").type(JsonFieldType.STRING).optional().description("매장 소개"),
                    fieldWithPath("openingDays[]").type(JsonFieldType.ARRAY).optional().description("영업일").attributes(openingDaysAttribute()),
                    fieldWithPath("openTime").type(JsonFieldType.STRING).optional().description("영업 시작시간"),
                    fieldWithPath("closeTime").type(JsonFieldType.STRING).optional().description("영업 종료시간"),
                    fieldWithPath("tags[]").type(JsonFieldType.ARRAY).optional().description("태그")
                ),
                responseFields(
                    defaultResponseFields()
                )
            ));
    }

    @Test
    @WithMockCustomUser
    void 매장_정보_수정() throws Exception {
        final StoreDTO storeDTO = initStoreDTO(1L, new HashSet<>(){{
            add(initStoreTagDTO(1L, TAG_TITLE1));
            add(initStoreTagDTO(2L, TAG_TITLE2));
        }});
        given(storeService.modifyStore(anyLong(), anyLong(), any(StoreDTO.class))).willReturn(storeDTO);

        final ModifyStorePayload.Request request = ModifyStorePayload.Request.builder()
            .storeStatus(STORE_STATUS)
            .storeName(STORE_NAME)
            .zipCode(ADDRESS.getZipCode())
            .defaultAddress(ADDRESS.getDefaultAddress())
            .detailAddress(ADDRESS.getDetailAddress())
            .latitude(ADDRESS.getLatitude())
            .longitude(ADDRESS.getLongitude())
            .tel(TEL)
            .homepageUrl(HOMEPAGE_URL)
            .reservationUrl(RESERVATION_URL)
            .introduction(INTRODUCTION)
            .openingDays(OPENING_DAYS)
            .openTime(OPEN_TIME)
            .closeTime(CLOSE_TIME)
            .tags(new HashSet<>(){{
                add(TAG_TITLE1);
                add(TAG_TITLE2);
            }})
            .build();
        final ResultActions result = mockMvc.perform(
                put("/store/{storeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(JWTUtil.AUTHORIZATION_HEADER, JWTUtil.BEARER_PREFIX + "{token}")
        );

        result.andExpect(status().isOk())
                .andDo(document("store/modify",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("매장 고유번호")
                        ),
                        requestFields(
                                fieldWithPath("storeStatus").type(JsonFieldType.STRING).description("매장 상태").attributes(storeStatusAttribute()),
                                fieldWithPath("storeName").type(JsonFieldType.STRING).description("매장 명"),
                                fieldWithPath("zipCode").type(JsonFieldType.STRING).description("우편 번호"),
                                fieldWithPath("defaultAddress").type(JsonFieldType.STRING).description("기본 주소"),
                                fieldWithPath("detailAddress").type(JsonFieldType.STRING).optional().description("상세 주소"),
                                fieldWithPath("latitude").type(JsonFieldType.NUMBER).description("위도"),
                                fieldWithPath("longitude").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("tel").type(JsonFieldType.STRING).description("연락처"),
                                fieldWithPath("homepageUrl").type(JsonFieldType.STRING).optional().description("홈페이지 URL"),
                                fieldWithPath("reservationUrl").type(JsonFieldType.STRING).optional().description("예약 사이트 URL"),
                                fieldWithPath("introduction").type(JsonFieldType.STRING).optional().description("매장 소개"),
                                fieldWithPath("openingDays[]").type(JsonFieldType.ARRAY).optional().description("영업일").attributes(openingDaysAttribute()),
                                fieldWithPath("openTime").type(JsonFieldType.STRING).optional().description("영업 시작시간"),
                                fieldWithPath("closeTime").type(JsonFieldType.STRING).optional().description("영업 종료시간"),
                                fieldWithPath("tags[]").type(JsonFieldType.ARRAY).optional().description("태그")
                        ),
                        responseFields(
                                defaultResponseFields()
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    void 매장_삭제() throws Exception {
        willDoNothing().given(storeService).deleteStore(anyLong(), anyLong());

        final ResultActions result = mockMvc.perform(
                delete("/store/{storeId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(JWTUtil.AUTHORIZATION_HEADER, JWTUtil.BEARER_PREFIX + "{token}")
        );

        result.andExpect(status().isOk())
                .andDo(document("store/delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("매장 고유번호")
                        ),
                        responseFields(
                                defaultResponseFields()
                        )
                ));
    }

    @Test
    void 매장_정보_조회() throws Exception {
        final StoreDTO storeDTO = initStoreDTO(1L, new HashSet<>(){{
            add(initStoreTagDTO(1L, TAG_TITLE1));
            add(initStoreTagDTO(2L, TAG_TITLE2));
        }});
        storeDTO.setProfileImages(new ArrayList<>(){{
            add(UploadFileDTO.builder()
                    .id(1L)
                    .originName("originFileName.jpg")
                    .uploadName("uploadFileName.jpg")
                    .fullPath("https://s3/upload/uploadFileName.jpg")
                    .path("/upload/uploadFileName.jpg")
                    .size(2541)
                    .ext("JPG")
                    .build());
        }});
        given(storeService.getStoreInfo(anyLong())).willReturn(storeDTO);

        final ResultActions result = mockMvc.perform(
                get("/store/{storeId}", 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(JWTUtil.AUTHORIZATION_HEADER, JWTUtil.BEARER_PREFIX + "{token}")
        );

        result.andExpect(status().isOk())
                .andDo(document("store/info",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("매장 고유번호")
                        ),
                        responseFields(
                                dataResponseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("매장 고유번호"),
                                        fieldWithPath("storeType").type(JsonFieldType.STRING).description("매장 유형").attributes(storeTypeAttribute()),
                                        fieldWithPath("storeStatus").type(JsonFieldType.STRING).description("매장 상태").attributes(storeStatusAttribute()),
                                        fieldWithPath("storeName").type(JsonFieldType.STRING).description("매장 명"),
                                        fieldWithPath("address").type(JsonFieldType.OBJECT).description("매장 주소 정보"),
                                        fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("우편 번호"),
                                        fieldWithPath("address.defaultAddress").type(JsonFieldType.STRING).description("기본 주소"),
                                        fieldWithPath("address.detailAddress").type(JsonFieldType.STRING).optional().description("상세 주소"),
                                        fieldWithPath("address.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                        fieldWithPath("address.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                        fieldWithPath("tel").type(JsonFieldType.STRING).description("연락처"),
                                        fieldWithPath("homepageUrl").type(JsonFieldType.STRING).optional().description("홈페이지 URL"),
                                        fieldWithPath("reservationUrl").type(JsonFieldType.STRING).optional().description("예약 사이트 URL"),
                                        fieldWithPath("introduction").type(JsonFieldType.STRING).optional().description("매장 소개"),
                                        fieldWithPath("openingDays").type(JsonFieldType.ARRAY).optional().description("영업일").attributes(openingDaysAttribute()),
                                        fieldWithPath("openTime").type(JsonFieldType.STRING).optional().description("영업 시작시간"),
                                        fieldWithPath("closeTime").type(JsonFieldType.STRING).optional().description("영업 종료시간"),
                                        fieldWithPath("tags[]").type(JsonFieldType.ARRAY).optional().description("태그") ,
                                        fieldWithPath("tags[].id").type(JsonFieldType.NUMBER).optional().description("태그 고유번호"),
                                        fieldWithPath("tags[].title").type(JsonFieldType.STRING).optional().description("태그 타이틀"),
                                        fieldWithPath("profileImages[]").type(JsonFieldType.ARRAY).optional().description("프로필 이미지"),
                                        fieldWithPath("profileImages[].id").type(JsonFieldType.NUMBER).optional().description("파일 고유번호"),
                                        fieldWithPath("profileImages[].originName").type(JsonFieldType.STRING).optional().description("원본 파일명"),
                                        fieldWithPath("profileImages[].uploadName").type(JsonFieldType.STRING).optional().description("업로드 파일명"),
                                        fieldWithPath("profileImages[].fullPath").type(JsonFieldType.STRING).optional().description("파일 전체 경로"),
                                        fieldWithPath("profileImages[].path").type(JsonFieldType.STRING).optional().description("파일 경로"),
                                        fieldWithPath("profileImages[].size").type(JsonFieldType.NUMBER).optional().description("파일 사이즈"),
                                        fieldWithPath("profileImages[].ext").type(JsonFieldType.STRING).optional().description("파일 확장자")
                                )
                        )
                ));
    }

    @Test
    void 회원_별_매장_목록_조회() throws Exception {
        final int size = 10;
        final int page = 1;
        final List<StoreListDTO> storeList = new ArrayList<>(){{
            add(initStoreListDTO(1L, new String[]{TAG_TITLE1, TAG_TITLE2}));
            add(initStoreListDTO(2L, new String[]{TAG_TITLE1, TAG_TITLE2}));
        }};
        PageDTO<StoreListDTO> storeListPage = new PageDTO<>(storeList, storeList.size(), size, page, 1);
        given(storeService.getStoreListByUserId(anyLong(), anyInt(), anyInt())).willReturn(storeListPage);

        final long userId = 1;
        final ResultActions result = mockMvc.perform(
                get("/store/user/{userId}", 1L)
                        .param("size", String.valueOf(size))
                        .param("page", String.valueOf(page))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(JWTUtil.AUTHORIZATION_HEADER, JWTUtil.BEARER_PREFIX + "{token}")
        );

        result.andExpect(status().isOk())
                .andDo(document("store/list-user",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("userId").description("회원 고유번호")
                        ),
                        requestParameters(
                                parameterWithName("size").description("한 페이지에 보일 데이터 수"),
                                parameterWithName("page").description("조회할 페이지")
                        ),
                        responseFields(
                                pagingResponseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("매장 고유번호"),
                                        fieldWithPath("storeType").type(JsonFieldType.STRING).description("매장 유형").attributes(storeTypeAttribute()),
                                        fieldWithPath("storeStatus").type(JsonFieldType.STRING).description("매장 상태").attributes(storeStatusAttribute()),
                                        fieldWithPath("storeName").type(JsonFieldType.STRING).description("매장 명"),
                                        fieldWithPath("address").type(JsonFieldType.OBJECT).description("매장 주소 정보"),
                                        fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("우편 번호"),
                                        fieldWithPath("address.defaultAddress").type(JsonFieldType.STRING).description("기본 주소"),
                                        fieldWithPath("address.detailAddress").type(JsonFieldType.STRING).optional().description("상세 주소"),
                                        fieldWithPath("address.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                        fieldWithPath("address.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                        fieldWithPath("tel").type(JsonFieldType.STRING).description("연락처"),
                                        fieldWithPath("homepageUrl").type(JsonFieldType.STRING).optional().description("홈페이지 URL"),
                                        fieldWithPath("reservationUrl").type(JsonFieldType.STRING).optional().description("예약 사이트 URL"),
                                        fieldWithPath("introduction").type(JsonFieldType.STRING).optional().description("매장 소개"),
                                        fieldWithPath("tags[]").type(JsonFieldType.ARRAY).optional().description("태그")
                                )
                        )
                ));
    }

    @Test
    void 매장_유형_별_매장_목록_조회() throws Exception {
        final int size = 10;
        final int page = 1;
        final List<StoreListDTO> storeList = new ArrayList<>(){{
            add(initStoreListDTO(1L, new String[]{TAG_TITLE1, TAG_TITLE2}));
            add(initStoreListDTO(2L, new String[]{TAG_TITLE1, TAG_TITLE2}));
        }};
        PageDTO<StoreListDTO> storeListPage = new PageDTO<>(storeList, storeList.size(), size, page, 1);
        given(storeService.getStoreListByType(any(StoreType.class), anyInt(), anyInt())).willReturn(storeListPage);

        final ResultActions result = mockMvc.perform(
                get("/store/type/{type}", StoreType.CAMP_GROUND)
                        .param("size", String.valueOf(size))
                        .param("page", String.valueOf(page))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(JWTUtil.AUTHORIZATION_HEADER, JWTUtil.BEARER_PREFIX + "{token}")
        );

        // Then
        result.andExpect(status().isOk())
                .andDo(document("store/list-type",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("type").description("매장 유형").attributes(storeTypeAttribute())
                        ),
                        requestParameters(
                                parameterWithName("size").description("한 페이지에 보일 데이터 수"),
                                parameterWithName("page").description("조회할 페이지")
                        ),
                        responseFields(
                                pagingResponseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("매장 고유번호"),
                                        fieldWithPath("storeType").type(JsonFieldType.STRING).description("매장 유형").attributes(storeTypeAttribute()),
                                        fieldWithPath("storeStatus").type(JsonFieldType.STRING).description("매장 상태").attributes(storeStatusAttribute()),
                                        fieldWithPath("storeName").type(JsonFieldType.STRING).description("매장 명"),
                                        fieldWithPath("address").type(JsonFieldType.OBJECT).description("매장 주소 정보"),
                                        fieldWithPath("address.zipCode").type(JsonFieldType.STRING).description("우편 번호"),
                                        fieldWithPath("address.defaultAddress").type(JsonFieldType.STRING).description("기본 주소"),
                                        fieldWithPath("address.detailAddress").type(JsonFieldType.STRING).optional().description("상세 주소"),
                                        fieldWithPath("address.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                        fieldWithPath("address.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                        fieldWithPath("tel").type(JsonFieldType.STRING).description("연락처"),
                                        fieldWithPath("homepageUrl").type(JsonFieldType.STRING).optional().description("홈페이지 URL"),
                                        fieldWithPath("reservationUrl").type(JsonFieldType.STRING).optional().description("예약 사이트 URL"),
                                        fieldWithPath("introduction").type(JsonFieldType.STRING).optional().description("매장 소개"),
                                        fieldWithPath("tags[]").type(JsonFieldType.ARRAY).optional().description("태그")
                                )
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    void 프로필_이미지_등록() throws Exception {
        willDoNothing().given(storeProfileImageService).updateProfileImages(anyLong(), anyLong(), anyList());

        MockMultipartFile multipartFile1 = new MockMultipartFile(
                "files",
                "profileImage1.jpg",
                "image/jpg",
                "uploadFile".getBytes());
        MockMultipartFile multipartFile2 = new MockMultipartFile(
                "files",
                "profileImage2.jpg",
                "image/jpg",
                "uploadFile".getBytes());

        final ResultActions result = mockMvc.perform(
                RestDocumentationRequestBuilders.fileUpload("/store/profile-image/{storeId}", 1)
                        .file(multipartFile1)
                        .file(multipartFile2)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(JWTUtil.AUTHORIZATION_HEADER, JWTUtil.BEARER_PREFIX + "{token}")
        );

        // Then
        result.andExpect(status().isOk())
                .andDo(document("store/profile-image",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("storeId").description("매장 고유번호")
                        ),
                        requestParts(
                                partWithName("files").description("첨부 이미지(복수 업로드 가능)")
                        ),
                        responseFields(
                                defaultResponseFields()
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    void 프로필_이미지_삭제() throws Exception {
        willDoNothing().given(storeProfileImageService).deleteProfileImages(anyLong(), anyLong(), any(Long[].class));

        final DeleteStoreProfileImagesPayload.Request request =
                new DeleteStoreProfileImagesPayload.Request(new Long[] {1L, 2L});
        final ResultActions result = mockMvc.perform(
                delete("/store/profile-image/{storeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(JWTUtil.AUTHORIZATION_HEADER, JWTUtil.BEARER_PREFIX + "{token}")
        );

        result.andExpect(status().isOk())
                .andDo(document("store/profile-image-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("fileIds[]").type(JsonFieldType.ARRAY).description("삭제할 파일 고유번호")
                        ),
                        responseFields(
                                defaultResponseFields()
                        )
                ));
    }

}