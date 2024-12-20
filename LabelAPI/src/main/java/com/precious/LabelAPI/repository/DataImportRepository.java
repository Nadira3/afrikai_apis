package com.precious.LabelAPI.repository;

import com.precious.LabelAPI.model.DataImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

// Data Import Repository
// Repository for data import metadata
// Extends JpaRepository for CRUD operations
@Repository
public interface DataImportRepository extends JpaRepository<DataImport, UUID> {

	// Find all imports by status
	List<DataImport> findByStatus(ImportStatus importStatus);

	// Find a specific import by filename
	DataImport findByFilename(String filename);
}
