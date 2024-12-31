import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.precious.TaskApi.service.ParserFactory;

// File Parser Service
@Service
public class TaskFileParserService {
    
    @Autowired
    private ObjectMapper objectMapper;
    
    
    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1).toLowerCase())
                .orElse("");
    }

    private Exam parseExamFile(MultipartFile file) throws IOException {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        List<QuestionAnswer> qaList = new ArrayList<>();
        
        switch (fileExtension) {
            case "csv":
                qaList = ParserFactory.parseCSV(file);
                break;
            case "txt":
                qaList = ParserFactory.parseTXT(file);
                break;
            case "json":
                return ParserFactory.parseExamJSON(file);
            case "xls":
            case "xlsx":
                qaList = ParserFactory.parseExcel(file);
                break;
            default:
                throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
        }
        
        Exam examTask = new Exam();
        examTask.setType("exam");
        examTask.setCreatedAt(new Date());
        examTask.setQuestionAnswers(qaList);
        return examTask;
    }

    public Task parseFile(MultipartFile file, String taskType) throws IOException {
        switch (taskType.toLowerCase()) {
            case "exam":
		    return parseExamFile(file);
            case "training":
                return parseTrainingFile(file);
            default:
                throw new IllegalArgumentException("Unsupported task type: " + taskType);
        }
    }
    
    private TrainingTask parseTrainingFile(MultipartFile file) throws IOException {
        TrainingTask trainingTask = new TrainingTask();
        trainingTask.setType("training");
        trainingTask.setCreatedAt(new Date());
        
        String instructions = new String(file.getBytes());
        trainingTask.setInstructions(instructions);
        
        return trainingTask;
    }
    
}
