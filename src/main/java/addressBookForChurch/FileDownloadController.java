package addressBookForChurch;

import addressBookForChurch.users.entity.Users;
import addressBookForChurch.users.repository.UsersRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class FileDownloadController {

    private final UsersRepository usersRepository;

    @GetMapping
    public ResponseEntity<byte[]> downloadUsersExcel() throws IOException {
        // Create Excel workbook and sheet
        List<Users> users = usersRepository.findAll();
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("UsersData");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Phone");
        headerRow.createCell(2).setCellValue("Prayer Note");
        headerRow.createCell(3).setCellValue("Picture");

        // Populate data rows
        int rowNum = 1;
        for (Users user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getName());
            row.createCell(1).setCellValue(user.getPhone());
            row.createCell(2).setCellValue(user.getPrayerNote());

            // Add picture if available
            if (user.getPicture() != null) {
                int pictureIndex = workbook.addPicture(user.getPicture(), Workbook.PICTURE_TYPE_JPEG);
                CreationHelper helper = workbook.getCreationHelper();
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(3);  // The column where the picture will be inserted
                anchor.setRow1(row.getRowNum());  // The row where the picture will be inserted
                Picture picture = drawing.createPicture(anchor, pictureIndex);
                picture.resize(0.5); // Resize the image to fit cell dimensions
            }
        }

        // Adjust column widths
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 10000);
        sheet.setColumnWidth(3, 8000);

        // Write to ByteArrayOutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        // Prepare file for download
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=UsersData.xlsx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(out.toByteArray());
    }
}
