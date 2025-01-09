package com.precious.LabelAPI.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.precious.LabelAPI.model.enums.FileType;
import com.precious.LabelAPI.model.enums.ImportStatus;
import com.precious.LabelAPI.util.FileManagerUtil;


/**
 * Represents a batch of data imported for labeling
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "data_imports")
public class DataImport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private FileType fileType;

    @Column(name = "total_records")
    private Integer totalRecords;

    @Column(name = "processed_records")
    private Integer processedRecords;

    @Column(name = "imported_at", nullable = false)
    private LocalDateTime importedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "import_status", nullable = false)
    private ImportStatus importStatus;

    @OneToMany(mappedBy = "dataImport", cascade = CascadeType.ALL)
    private List<PromptResponsePair> promptResponsePairs;

    // Constructor for creating a new data import
    // The import status is set to PENDING by default
    // The importedAt field is set to the current date and time
    // The processedRecords field is set to 0 by default
    // The totalRecords field is set to the number of prompt-response pairs
    // The promptResponsePairs field is set to the list of prompt-response pairs
    // The fileType field is set to the file type of the imported file
    public DataImport(
        String fileName,
        FileType filetype
    ) {
        this.fileName = fileName;
        this.filePath = FileManagerUtil.getCurrentDirectoryFile(fileName).getAbsolutePath();
        this.fileType = filetype;
        this.importedAt = LocalDateTime.now();
        this.promptResponsePairs = new ArrayList<>(promptResponsePairs);
        this.processedRecords = 0;
        this.totalRecords = promptResponsePairs.size();
    }
}
