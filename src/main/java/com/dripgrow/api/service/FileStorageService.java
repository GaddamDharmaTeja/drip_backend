package com.dripgrow.api.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFsBucket;

    public FileStorageService(GridFsTemplate gridFsTemplate, GridFSBucket gridFsBucket) {
        this.gridFsTemplate = gridFsTemplate;
        this.gridFsBucket = gridFsBucket;
    }

    public Map<String, Object> storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file uploaded");
        }
        String contentType = file.getContentType() == null
                ? ""
                : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed");
        }
        if (file.getSize() > 8 * 1024 * 1024L) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image must be under 8 MB");
        }

        try (InputStream in = file.getInputStream()) {
            ObjectId id = gridFsTemplate.store(
                    in,
                    file.getOriginalFilename() != null ? file.getOriginalFilename() : "image",
                    contentType
            );
            Map<String, Object> result = new HashMap<>();
            result.put("id", id.toHexString());
            result.put("filename", file.getOriginalFilename());
            result.put("contentType", contentType);
            result.put("size", file.getSize());
            result.put("url", "/api/files/" + id.toHexString());
            return result;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store image");
        }
    }

    public LoadedFile load(String id) {
        GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(id))));
        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found");
        }
        GridFSDownloadStream stream = gridFsBucket.openDownloadStream(file.getObjectId());
        GridFsResource resource = new GridFsResource(file, stream);
        String contentType = file.getMetadata() != null && file.getMetadata().getString("_contentType") != null
                ? file.getMetadata().getString("_contentType")
                : (file.getMetadata() != null && file.getMetadata().getString("contentType") != null
                        ? file.getMetadata().getString("contentType")
                        : "application/octet-stream");
        // GridFsTemplate stores content type in metadata under _contentType by default in some versions;
        // also check resource content type
        try {
            if (resource.getContentType() != null) {
                contentType = resource.getContentType();
            }
        } catch (Exception ignored) {
            /* keep metadata value */
        }
        return new LoadedFile(resource, contentType, file.getFilename(), file.getLength());
    }

    public void delete(String id) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(new ObjectId(id))));
    }

    public record LoadedFile(GridFsResource resource, String contentType, String filename, long length) {
    }
}
