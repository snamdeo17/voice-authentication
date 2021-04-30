package com.mastercard.authentication.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mastercard.authentication.constant.Constants;
import com.mastercard.authentication.services.IAuthenticateService;

@RestController
public class FileController {

	@Value("${server.address:localhost}")
	private String host;
	@Value("${server.port:8088}")
	private String port;

	@Autowired
	IAuthenticateService authenticationService;

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	String tempPath = System.getProperty(Constants.USER_HOME) + File.separatorChar
			+ Constants.VOICE_MASTER_AUTHENTICATION + File.separatorChar + Constants.HISTORY + File.separatorChar;

	@GetMapping("/user/files/{userId}")
	public List<String> getListFiles(@PathVariable int userId) {
		String tempPath2 = tempPath + userId;
		Path filePath = Paths.get(tempPath2);
		try {
			authenticationService.getUserAuthHistory(userId);
			List<String> list = Files.walk(filePath, 1).map(f -> Integer.parseInt(f.toFile().getName().replace(".wav", ""))).sorted().map(d -> d.toString())
					.collect(Collectors.toList());
			list.add("http://" + host + ":" + port + "/user/download/" + userId + "/<file-name>.wav");
			return list;
		} catch (IOException e) {
			throw new RuntimeException("Error! -> message = " + e.getMessage());
		}
	}

	@GetMapping("/user/download/{userId}/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable int userId, @PathVariable String fileName,
			HttpServletRequest request) throws Exception {
		// Load file as Resource
		Resource resource = loadFileAsResource(userId, fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	public Resource loadFileAsResource(int id, String fileName) throws Exception {
		String tempPath2 = tempPath + id;
		try {
			Path filePath = Paths.get(tempPath2 + File.separatorChar + fileName);
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new Exception("File not found " + tempPath2);
			}
		} catch (MalformedURLException ex) {
			throw new Exception("File not found " + tempPath2, ex);
		}
	}
}
