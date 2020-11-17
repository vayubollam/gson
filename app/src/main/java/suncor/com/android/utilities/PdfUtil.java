package suncor.com.android.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

public class PdfUtil {
    static public File createPdf(Context context, List<String> data, String fileName){
        // create a new document
        PdfDocument document = new PdfDocument();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        float height = paint.descent() - paint.ascent();
        float width = 0;
        for (String line: data) {
            float tempWidth = paint.measureText(line);

            if (tempWidth > width)
                width = tempWidth;
        }

        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                Math.round(width) + 20,
                Math.round(data.size() * height) + 100,
                1
        ).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int x = 10, y = 50;
        for (String line: data) {
            canvas.drawText(line, x, y, paint);
            y += height;
        }

        // finish the page
        document.finishPage(page);

        // write the document content
        String targetPdf = context.getExternalFilesDir(DIRECTORY_DOCUMENTS).getPath() + "/" + fileName + ".pdf";
        File filePath = new File(targetPdf);

        try {
            document.writeTo(new FileOutputStream(filePath));
        } catch (IOException e) {
            Timber.e("main", "error "+e.toString());
            return null;
        }

        // close the document
        document.close();

        return filePath;
    }
}
