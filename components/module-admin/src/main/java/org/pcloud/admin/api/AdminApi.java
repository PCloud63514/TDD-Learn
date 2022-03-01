package org.pcloud.admin.api;

import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.domain.Admin;
import org.pcloud.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("admin")
@RestController
public class AdminApi {
    private final AdminService adminService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping("join")
    public Admin joinAdmin(@RequestBody AdminJoinRequest request) {
        return adminService.joinAdmin(request);
    }

    @GetMapping
    public void getAdmins() {

    }
//    @Test
//    void getTodos_returnsOkHttpStatus() throws Exception {
//        mockMvc.perform(get("/todos"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void getTodos_returnsTodos() throws Exception {
//        spyTodoService.getTodos_returnValue = List.of(
//                new Todo("title1", "description1", LocalDateTime.of(2022, 2, 22, 20, 20, 20))
//        );
//
//        mockMvc.perform(get("/todos"))
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0].title", equalTo("title1")))
//                .andExpect(jsonPath("$[0].description", equalTo("description1")))
//                .andExpect(jsonPath("$[0].createAt", equalTo("2022-02-22 20:20:20")))
//        ;


//
//    @Test
//    void postTodo_returnsCreatedHttpStatus() throws Exception {
//        mockMvc.perform(post("/todos")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isCreated());
//    }
//
//@Test
//void postTodo_passesTitleAndDescriptionToService() throws Exception {
//    TodoCreateRequest givenRequest = new TodoCreateRequest("title1", "description1");
//
//    mockMvc.perform(post("/todos")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(givenRequest)));
//
//    assertThat(spyTodoService.postTodo_argumentRequest.getTitle()).isEqualTo("title1");
//    assertThat(spyTodoService.postTodo_argumentRequest.getDescription()).isEqualTo("description1");
//}
}
