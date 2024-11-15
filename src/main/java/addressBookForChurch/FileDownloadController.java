package addressBookForChurch;

import addressBookForChurch.users.entity.Users;
import addressBookForChurch.users.repository.UsersRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRun;
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
        List<Users> users = usersRepository.findAll();
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (Users user : users) {
            var paragraph = document.createParagraph();

            // 이미지가 있을 경우 먼저 추가
            if (user.getPicture() != null) {
                var imageRun = paragraph.createRun();
                ByteArrayInputStream pictureInputStream = new ByteArrayInputStream(user.getPicture());
                BufferedImage bufferedImage = ImageIO.read(pictureInputStream);

                int originalWidth = bufferedImage.getWidth();
                int originalHeight = bufferedImage.getHeight();
                int targetWidth = 150;
                int targetHeight = 150;

                if (originalWidth > originalHeight) {
                    targetHeight = (int) ((double) originalHeight / originalWidth * targetWidth);
                } else {
                    targetWidth = (int) ((double) originalWidth / originalHeight * targetHeight);
                }

                imageRun.addPicture(
                    new ByteArrayInputStream(user.getPicture()),
                    XWPFDocument.PICTURE_TYPE_JPEG,
                    "picture.jpg",
                    Units.toEMU(targetWidth),
                    Units.toEMU(targetHeight)
                );
                imageRun.addBreak();
            }

            XWPFRun boldRun = paragraph.createRun();
            boldRun.setBold(true);
            boldRun.setText("이름: ");
            boldRun.addBreak();

            XWPFRun normalRun = paragraph.createRun();
            normalRun.setBold(false);
            normalRun.setText(user.getName());
            normalRun.addBreak();

            XWPFRun boldRunPrayer = paragraph.createRun();
            boldRunPrayer.setBold(true);
            boldRunPrayer.setText("기도제목: ");
            boldRunPrayer.addBreak();

            if (user.getPrayerNote() != null) {
                String[] lines = user.getPrayerNote().split("\n");
                for (String line : lines) {
                    XWPFRun prayerRun = paragraph.createRun();
                    prayerRun.setBold(false);
                    prayerRun.setText(line);
                    prayerRun.addBreak();
                }
            }
        }

        document.write(out);
        document.close();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=UsersData.docx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(out.toByteArray());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<byte[]> downloadOneUserDoc(@PathVariable("userId") Long userId) throws IOException, InvalidFormatException {
        Users user = usersRepository.findById(userId).orElseThrow();
        XWPFDocument document = new XWPFDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        var paragraph = document.createParagraph();

        // 이미지가 있을 경우 먼저 추가
        if (user.getPicture() != null) {
            var imageRun = paragraph.createRun();
            ByteArrayInputStream pictureInputStream = new ByteArrayInputStream(user.getPicture());
            BufferedImage bufferedImage = ImageIO.read(pictureInputStream);

            int originalWidth = bufferedImage.getWidth();
            int originalHeight = bufferedImage.getHeight();
            int targetWidth = 150;
            int targetHeight = 150;

            if (originalWidth > originalHeight) {
                targetHeight = (int) ((double) originalHeight / originalWidth * targetWidth);
            } else {
                targetWidth = (int) ((double) originalWidth / originalHeight * targetHeight);
            }

            imageRun.addPicture(
                new ByteArrayInputStream(user.getPicture()),
                XWPFDocument.PICTURE_TYPE_JPEG,
                "picture.jpg",
                Units.toEMU(targetWidth),
                Units.toEMU(targetHeight)
            );
            imageRun.addBreak();
        }

        XWPFRun boldRun = paragraph.createRun();
        boldRun.setBold(true);
        boldRun.setText("이름: ");
        boldRun.addBreak();

        XWPFRun normalRun = paragraph.createRun();
        normalRun.setBold(false);
        normalRun.setText(user.getName());
        normalRun.addBreak();

        XWPFRun boldRunPrayer = paragraph.createRun();
        boldRunPrayer.setBold(true);
        boldRunPrayer.setText("기도제목: ");
        boldRunPrayer.addBreak();

        if (user.getPrayerNote() != null) {
            String[] lines = user.getPrayerNote().split("\n");
            for (String line : lines) {
                XWPFRun prayerRun = paragraph.createRun();
                prayerRun.setBold(false);
                prayerRun.setText(line);
                prayerRun.addBreak();
            }
        }
        document.write(out);
        document.close();

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=UserData_" + user.getId() + ".docx")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(out.toByteArray());
    }
}
