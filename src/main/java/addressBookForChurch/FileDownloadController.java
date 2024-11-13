package addressBookForChurch;

import addressBookForChurch.users.entity.Users;
import addressBookForChurch.users.repository.UsersRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class FileDownloadController {

    private final UsersRepository usersRepository;

    @GetMapping
    public ResponseEntity<byte[]> downloadUsersDoc() throws IOException, InvalidFormatException {
        // DOCX 파일 생성
        List<Users> users = usersRepository.findAll();
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (Users user : users) {
            var paragraph = document.createParagraph();
            var run = paragraph.createRun();
            run.setText("이름: " + user.getName());
            run.addBreak();
            run.setText("전화번호: " + user.getPhone());
            run.addBreak();
            run.setText("기도 제목: " + user.getPrayerNote());
            run.addBreak();
            if (user.getPicture() != null) {
                var imageRun = document.createParagraph().createRun();
                imageRun.addPicture(new ByteArrayInputStream(user.getPicture()),
                    XWPFDocument.PICTURE_TYPE_JPEG,
                    "picture.jpg",
                    Units.toEMU(150),
                    Units.toEMU(150));
            }
            run.addBreak();
        }

        document.write(out);
        document.close();

        // 파일 다운로드를 위한 HTTP 응답 생성
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=UsersData.docx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(out.toByteArray());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> downloadOneUserDoc(@PathVariable("userId") Long userId) throws IOException, InvalidFormatException {
        // DOCX 파일 생성
        Users user = usersRepository.findById(userId).orElseThrow();
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        var paragraph = document.createParagraph();
        var run = paragraph.createRun();
        run.setText("이름: " + user.getName());
        run.addBreak();
        run.setText("전화번호: " + user.getPhone());
        run.addBreak();
        run.setText("기도제목: " + user.getPrayerNote());
        run.addBreak();
        if (user.getPicture() != null) {
            var imageRun = document.createParagraph().createRun();
            imageRun.addPicture(new ByteArrayInputStream(user.getPicture()),
                XWPFDocument.PICTURE_TYPE_JPEG,
                "picture.jpg",
                Units.toEMU(150),
                Units.toEMU(150));
        }
        run.addBreak();

        document.write(out);
        document.close();

        // 파일 다운로드를 위한 HTTP 응답 생성
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=UsersData.docx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(out.toByteArray());
    }
}
