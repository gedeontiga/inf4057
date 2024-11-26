// package com.m1fonda.service_bank;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.m1fonda.service_bank.controller.BankController;
// import com.m1fonda.service_bank.dto.AgencyDTO;
// import com.m1fonda.service_bank.dto.BankDTO;
// import com.m1fonda.service_bank.dto.BankDTOResponse;
// import com.m1fonda.service_bank.dto.BankWithAgenciesDTO;
// import com.m1fonda.service_bank.model.BankModel;
// import com.m1fonda.service_bank.service.BankService;

// // import lombok.AllArgsConstructor;
// import lombok.RequiredArgsConstructor;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import java.time.LocalDateTime;
// import java.util.Date;
// import java.util.HashSet;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @RequiredArgsConstructor

// @WebMvcTest(BankController.class)
// public class BankControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private BankService bankService;

//     @Autowired
//     private ObjectMapper objectMapper;

//     private BankDTO validBankDTO;
//     private BankDTOResponse bankDTOResponse;
//     private BankWithAgenciesDTO bankWithAgenciesDTO;

//     @BeforeEach
//     void setUp() {
//         validBankDTO = new BankDTO(
//             "Test Bank", 
//             "logo.png", 
//             "owner@example.com", 
//             "Commercial", 
//             1000000.0, 
//             "1234567890"
//         );

//         bankDTOResponse = new BankDTOResponse(
//             "Test Bank", 
//             "logo.png", 
//             "owner@example.com", 
//             "Commercial", 
//             1000000.0, 
//             "1234567890", 
//             "12345678", 
//             new Date()
//         );

//         bankWithAgenciesDTO = new BankWithAgenciesDTO(
//             new BankModel(), 
//             new HashSet<>()
//         );
//     }

//     @Test
//     void newBank_ValidInput_ReturnsCreatedBank() throws Exception {
//         when(bankService.createBank(any(BankDTO.class))).thenReturn(bankDTOResponse);

//         mockMvc.perform(post("/api/bank/new")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(validBankDTO)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.name").value("Test Bank"))
//                 .andExpect(jsonPath("$.bankNumber").exists());
//     }

//     @Test
//     void getBank_ExistingBank_ReturnsBank() throws Exception {
//         String bankNumber = "12345678";
//         when(bankService.getBank(bankNumber)).thenReturn(bankWithAgenciesDTO);

//         mockMvc.perform(get("/api/bank/get/{param}", bankNumber)
//                 .param("param", bankNumber))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//     }

//     @Test
//     void updateAgency_ValidAgency_ReturnsUpdatedAgency() throws Exception {
//         AgencyDTO agencyDTO = new AgencyDTO("1", "an agency", 400000, 1, 1, "address", "123456789");
//         when(bankService.updateAgency(any(AgencyDTO.class))).thenReturn(agencyDTO);

//         mockMvc.perform(put("/api/bank/update")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(agencyDTO)))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.name").value("Updated Agency"));
//     }

//     @Test
//     void addAgency_ValidAgency_ReturnsUpdatedBank() throws Exception {
//         AgencyDTO agencyDTO = new AgencyDTO("2", "an agency 2", 400000, 1, 1, "address", "123456789");
//         when(bankService.addAgency(any(AgencyDTO.class))).thenReturn(bankWithAgenciesDTO);

//         mockMvc.perform(post("/api/bank/agency/add")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(agencyDTO)))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     void removeAgency_ValidAgency_ReturnsBoolean() throws Exception {
//         AgencyDTO agencyDTO = new AgencyDTO("3", "an agency 3", 400000, 1, 1, "address", "123456789");
//         when(bankService.removeAgency(any(AgencyDTO.class))).thenReturn(true);

//         mockMvc.perform(post("/api/bank/agency/remove")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(agencyDTO)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("true"));
//     }
// }
