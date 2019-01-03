package com.xyz.upload.demo.web.rest;

import static com.xyz.upload.demo.web.rest.TestUtil.createFormattingConversionService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.google.common.io.Resources;
import com.xyz.upload.demo.FileUploadDemoApp;
import com.xyz.upload.demo.service.StudentService;
import com.xyz.upload.demo.web.rest.errors.ExceptionTranslator;

/**
 * Test class for the UploadResource REST controller.
 *
 * @see UploadResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileUploadDemoApp.class)
public class UploadResourceIntTest {

	private MockMvc restMockMvc;

	@Autowired
	private StudentService studentService;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private Validator validator;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		final UploadResource uploadResource = new UploadResource(studentService);
		this.restMockMvc = MockMvcBuilders.standaloneSetup(uploadResource)
				.setCustomArgumentResolvers(pageableArgumentResolver).setControllerAdvice(exceptionTranslator)
				.setConversionService(createFormattingConversionService()).setMessageConverters(jacksonMessageConverter)
				.setValidator(validator).build();
	}

	/**
	 * Test uploadFile
	 */
	@Test
	public void testUploadFile() throws Exception {

		FileInputStream fis = new FileInputStream(new File(Resources.getResource("students.csv").getFile()));

		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "students.csv", "text/plain", fis);

		restMockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload/upload-file").file(mockMultipartFile))
				.andExpect(status().isOk());

	}

	@Test
	public void testUploadFile_WithEmptyFile() throws Exception {

		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "abc.csv", "text/plain", "".getBytes());

		restMockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload/upload-file").file(mockMultipartFile))
				.andExpect(status().is4xxClientError());

	}

	@Test
	public void testUploadFile_WithWrongParamFile() throws Exception {

		FileInputStream fis = new FileInputStream(new File(Resources.getResource("students.csv").getFile()));

		MockMultipartFile mockMultipartFile = new MockMultipartFile("XYZ", "students.csv", "text/plain", fis);

		restMockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload/upload-file").file(mockMultipartFile))
				.andExpect(status().is4xxClientError());

	}

	@Test
	public void testUploadFile_WithUnsupportedExtension() throws Exception {

		FileInputStream fis = new FileInputStream(new File(Resources.getResource("students.csv").getFile()));

		MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "students.xyz", "text/plain", fis);

		restMockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload/upload-file").file(mockMultipartFile))
				.andExpect(status().is4xxClientError());

	}
}
