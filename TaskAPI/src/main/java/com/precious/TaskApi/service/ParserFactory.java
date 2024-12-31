package com.precious.TaskApi.service;

import com.precious.TaskApi.model.Task;

public class ParserFactory {

    private ExamTask parseExamFile(MultipartFile file) throws IOException {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        List<QuestionAnswer> qaList = new ArrayList<>();
        
        switch (fileExtension) {
            case "csv":
                qaList = parseCSV(file);
                break;
            case "txt":
                qaList = parseTXT(file);
                break;
            case "json":
                return parseExamJSON(file);
            case "xls":
            case "xlsx":
                qaList = parseExcel(file);
                break;
            default:
                throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
        }
        
        ExamTask examTask = new ExamTask();
        examTask.setType("exam");
        examTask.setCreatedAt(new Date());
        examTask.setQuestionAnswers(qaList);
        return examTask;
    }
    
    private List<QuestionAnswer> parseExcel(MultipartFile file) throws IOException {
        List<QuestionAnswer> qaList = new ArrayList<>();
        
        try (InputStream is = file.getInputStream();
             Workbook workbook = createWorkbook(file)) {
            
            Sheet sheet = workbook.getSheetAt(0); // Get first sheet
            Iterator<Row> rowIterator = sheet.iterator();
            
            // Skip header row if exists
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            
            // Process each row
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Cell questionCell = row.getCell(0);
                Cell answerCell = row.getCell(1);
                
                if (questionCell != null && answerCell != null) {
                    String question = getCellValueAsString(questionCell);
                    String answer = getCellValueAsString(answerCell);
                    
                    if (!question.isEmpty() && !answer.isEmpty()) {
                        qaList.add(new QuestionAnswer(question, answer));
                    }
                }
            }
        }
        return qaList;
    }
    
    private Workbook createWorkbook(MultipartFile file) throws IOException {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        if ("xlsx".equals(fileExtension)) {
            return new XSSFWorkbook(file.getInputStream());
        } else if ("xls".equals(fileExtension)) {
            return new HSSFWorkbook(file.getInputStream());
        }
        throw new IllegalArgumentException("Invalid Excel file format");
    }
    
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue()).trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue()).trim();
                }
            default:
                return "";
        }
    }
    
    private List<QuestionAnswer> parseCSV(MultipartFile file) throws IOException {
        List<QuestionAnswer> qaList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    qaList.add(new QuestionAnswer(parts[0].trim(), parts[1].trim()));
                }
            }
        }
        return qaList;
    }
    
    private List<QuestionAnswer> parseTXT(MultipartFile file) throws IOException {
        List<QuestionAnswer> qaList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String question = null;
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Q:")) {
                    question = line.substring(2).trim();
                } else if (line.startsWith("A:") && question != null) {
                    String answer = line.substring(2).trim();
                    qaList.add(new QuestionAnswer(question, answer));
                    question = null;
                }
            }
        }
        return qaList;
    }
    
    private ExamTask parseExamJSON(MultipartFile file) throws IOException {
        return objectMapper.readValue(file.getInputStream(), ExamTask.class);
    }
    
}
