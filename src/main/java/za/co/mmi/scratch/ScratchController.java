package za.co.mmi.scratch;

import org.apache.poi.xslf.usermodel.*;
import org.json.JSONArray;
import org.json.JSONObject;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/proto")
public class ScratchController {

    private static final Logger log = LoggerFactory.getLogger(ScratchController.class);

    @RequestMapping(value="/hello", method= RequestMethod.POST)
    public ResponseEntity<String> hello(@RequestBody String message) {
        System.out.println(" -- Message is " + message);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value="/getppt", method= RequestMethod.POST)
    public ResponseEntity<byte[]> getPDF(@RequestBody String json) {
        log.debug(" -- received json payload " + json);

        ByteArrayOutputStream bout = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation"));
        headers.setContentDispositionFormData("export.pptx", "export.pptx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        try {

            bout = new ByteArrayOutputStream();
            XMLSlideShow ppt = new XMLSlideShow();
            XSLFSlideMaster defaultMaster = ppt.getSlideMasters().get(0);
            XSLFSlideLayout layout = defaultMaster.getLayout(SlideLayout.TITLE_AND_CONTENT);

            JSONArray jsonarray = new JSONArray(json);
            for (int i = 0; i < jsonarray.length(); i++) {

                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String title = jsonobject.getString("title");
                log.debug(" -- title " + title);
                String content = jsonobject.getString("content");
                log.debug(" -- content:" + content);

                XSLFSlide blankSlide = ppt.createSlide(layout);

                XSLFTextShape titleShape = blankSlide.getPlaceholder(0);
                XSLFTextShape contentShape = blankSlide.getPlaceholder(1);

                titleShape.setText(title);
                contentShape.setText(content);

            }

            ppt.write(bout);
            bout.flush();

            return new ResponseEntity<>(bout.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error(" -- error writing tmp ppt", e);
        } finally {
            try {
                bout.close();
            } catch (IOException e) {
                //squash
            }
        }

        return new ResponseEntity<>(null, headers, HttpStatus.NO_CONTENT);
    }

}
