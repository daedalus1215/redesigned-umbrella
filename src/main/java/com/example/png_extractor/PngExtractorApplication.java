package com.example.png_extractor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

@SpringBootApplication
public class PngExtractorApplication {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: PdfTextExtractorApp <input-folder-name>");
            System.exit(1);
        }

        String inputFolderName = args[0];
        String inputFolderPath = "flatten_input/"  + inputFolderName + "_images";
        String outputFolderPath = "text_output/" + inputFolderName + "/" + inputFolderName + "_images/";

        File inputFolder = new File(inputFolderPath);
        File outputFolder = new File(outputFolderPath);

        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            System.err.println("Input folder does not exist or is not a directory.");
            System.exit(1);
        }

        if (!outputFolder.exists()) {
            System.err.println("Output folder does not exist.");
            System.exit(1);
        }

        File[] pdfFiles = inputFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (pdfFiles == null || pdfFiles.length == 0) {
            System.err.println("No PDF files found in the input folder.");
            System.exit(1);
        }

        for (File pdfFile : pdfFiles) {
            try {
                extractTextFromPdf(pdfFile, outputFolder);
            } catch (IOException e) {
                System.err.println("Failed to process " + pdfFile.getName() + ": " + e.getMessage());
            }
        }
    }

    private static void extractTextFromPdf(File pdfFile, File outputFolder) throws IOException {
        String baseName = FilenameUtils.removeExtension(pdfFile.getName());
        File textFile = new File(outputFolder, baseName + ".txt");

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(pdfFile))) {
            int numberOfPages = pdfDocument.getNumberOfPages();
            StringBuilder textBuilder = new StringBuilder();

            for (int i = 1; i <= numberOfPages; i++) {
                String pageText = PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i), new LocationTextExtractionStrategy());
                textBuilder.append("Page ").append(i).append(":\n").append(pageText).append("\n\n");
            }

            java.nio.file.Files.write(textFile.toPath(), textBuilder.toString().getBytes());
            System.out.println("Text extracted to: " + textFile.getAbsolutePath());
        }
    }
}
