package org.pcloud.smartpass.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DepartmentApiTest {
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(DepartmentApi.class).build();
    }

    @Test
    void createDepartment_returnCreatedHttpStatus() throws Exception {
        mockMvc.perform(post("/departments"))
                .andExpect(status().isCreated());
    }
}