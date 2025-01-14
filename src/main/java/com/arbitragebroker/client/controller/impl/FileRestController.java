package com.arbitragebroker.client.controller.impl;

import com.arbitragebroker.client.model.FileModel;
import com.arbitragebroker.client.model.PageModel;
import com.arbitragebroker.client.util.DateUtil;
import com.arbitragebroker.client.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/files")
public class FileRestController {
    public static final String UPLOAD_DIR = "uploads/";

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity uploadFile(@RequestPart("file") MultipartFile file) {
        try {
            // Ensure the upload directory exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save the file to the file system
            String fileName = UUID.randomUUID().toString() + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.write(filePath, file.getBytes());

            // Return the URL where the file can be accessed
            var map = new HashMap<String,String>();
            map.put("url","/api/v1/files/" + fileName);
            return ResponseEntity.ok(map);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }
    @GetMapping("/findAllTable")
    public ResponseEntity<PageModel> findAllTable(@RequestParam Optional<String> name, @PageableDefault Pageable pageable) {
        File folder = new File(UPLOAD_DIR);
        File[] files = folder.listFiles();

        if (files == null) {
            return ResponseEntity.ok(new PageModel(0,0,null));
        }

        List<FileModel> list = Arrays.stream(files)
                .filter(File::isFile) // Only include files, not directories
                .map(file -> new FileModel(file.getName(), DateUtil.toLocalDateTime(file.lastModified())))
                .filter(f -> name.isEmpty() || f.getName().contains(name.get()))
                .sorted(getFileModelComparator(pageable))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        var pageResult = list.subList(start, end);
        return ResponseEntity.ok(new PageModel(files.length, list.size(), pageResult));
    }
    private Comparator<FileModel> getFileModelComparator(Pageable pageable) {
        Sort.Order sortOrder = pageable.getSort().getOrderFor("modifiedDate");
        if (sortOrder != null) {
            if (sortOrder.getDirection() == Sort.Direction.DESC) {
                return Comparator.comparing(FileModel::getModifiedDate).reversed();
            } else {
                return Comparator.comparing(FileModel::getModifiedDate);
            }
        }

        sortOrder = pageable.getSort().getOrderFor("name");
        if (sortOrder != null && sortOrder.getDirection() == Sort.Direction.DESC) {
            return Comparator.comparing(FileModel::getName).reversed();
        } else {
            return Comparator.comparing(FileModel::getName);
        }
    }
    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            byte[] bytes = Files.readAllBytes(filePath);

            // Determine the file's content type
            String contentType = Files.probeContentType(filePath);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(bytes);
        } catch (IOException e) {
            throw new NotFoundException(String.format("image %s not found", fileName));
        }
    }
    @DeleteMapping("/{fileName}")
    public ResponseEntity delete(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.deleteIfExists(filePath);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            throw new NotFoundException(String.format("file %s not found", fileName));
        }
    }
}
