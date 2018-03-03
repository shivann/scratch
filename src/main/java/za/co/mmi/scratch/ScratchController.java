package za.co.mmi.scratch;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/proto")
public class ScratchController {

    private static final Logger log = LoggerFactory.getLogger(ScratchController.class);

    @RequestMapping(value="/getppt", method= RequestMethod.GET, consumes = { "application/json" })
    public ResponseEntity<byte[]> getPDF(@RequestBody String json) {
        log.debug(" -- received json payload " + json);
        FileOutputStream fout = null;

        try {

            File file = new File("/tmp/tmp.pptx");
            fout = new FileOutputStream(file);

            XMLSlideShow ppt = new XMLSlideShow();
            XSLFSlide blankSlide = ppt.createSlide();

            ppt.write(fout);
            fout.flush();

        } catch (Exception e) {
            log.error(" -- error writing tmp ppt", e);
        } finally {
            try {
                fout.close();
            } catch (IOException e) {
                //squash
            }
        }

//        PdfUtil.showHelp(emp);

//        byte[] contents = (...);

        byte[] contents = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);

        return response;
    }

}
