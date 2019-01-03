package com.xyz.upload.demo.web.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xyz.upload.demo.service.StudentService;
import com.xyz.upload.demo.service.dto.StudentDTO;
import com.xyz.upload.demo.web.rest.errors.BadRequestAlertException;

/**
 * UploadResource controller
 */
@RestController
@RequestMapping("/api/upload")
public class UploadResource {

	private final Logger log = LoggerFactory.getLogger(UploadResource.class);

	private final String[] HEADERS = { "First Name", "Last Name", "Age" };

	@Autowired
	private StudentService studentService;

	public UploadResource(StudentService studentService) {
		super();
		this.studentService = studentService;
	}

	/**
	 * POST uploadFile
	 */
	@PostMapping("/upload-file")
	public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) throws URISyntaxException {

		log.info("INSIDE::UPLOAD");
		
		String extension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (!extension.equalsIgnoreCase("csv"))
			throw new BadRequestAlertException("Unsupported file extension. File is not a CSV.", file.getName(), "");

		if (file.isEmpty())
			throw new BadRequestAlertException("File is NULL or Empty.", file.getName(), "");

		List<StudentDTO> validRecords = new ArrayList<>();
		List<String> badRecords = new ArrayList<>();

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();

		Iterable<CSVRecord> records;
		try {
			records = CSVFormat.DEFAULT.withHeader(HEADERS).withFirstRecordAsHeader().withIgnoreEmptyLines(true)
					.parse(new InputStreamReader(file.getInputStream()));

			for (CSVRecord csvRecord : records) {

				String firstName = StringUtils.trimToEmpty(csvRecord.get(0));
				String lastName = StringUtils.trimToEmpty(csvRecord.get(1));
				Integer age = NumberUtils.toInt(csvRecord.get(2), 0);

				StudentDTO studentObj = new StudentDTO(firstName, lastName, age);
				Set<ConstraintViolation<StudentDTO>> violations = validator.validate(studentObj);

				if (violations.isEmpty()) {
					studentObj = studentService.save(studentObj);
					validRecords.add(studentObj);
				} else {
					// Collecting Bad Records with validation error messages
					String errorMsg = "";
					for (ConstraintViolation<StudentDTO> violation : violations) {
						if (errorMsg != "")
							errorMsg = errorMsg + "  &  " + violation.getMessage();
						else
							errorMsg = errorMsg + violation.getMessage();
					}
					badRecords.add(csvRecord + "   ::   " + errorMsg);
				}
			}
		} catch (IOException e) {
			log.error("Upload File Exception :: ", e);
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Upload File Exception :: ", e);
			e.printStackTrace();
		}

		log.info("INSIDE::UPLOAD::AFTER PARSING");

		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("Successfully Inserted", validRecords);
		responseMap.put("Bad Records", badRecords);

		return responseMap;
	}

}
