package util;

import javax.swing.text.*;
import java.nio.charset.StandardCharsets;

// 글자수 제한 필터
public class ByteLimitFilter extends DocumentFilter {

    private final int maxBytes;

    public ByteLimitFilter(int maxBytes) {
        this.maxBytes = maxBytes;
    }

    private int getByteLength(String s) {
        if (s == null) return 0;
        return s.getBytes(StandardCharsets.UTF_8).length;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {

        if (string == null) return;

        String oldText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText =
                oldText.substring(0, offset) +
                string +
                oldText.substring(offset);

        if (getByteLength(newText) <= maxBytes) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr)
            throws BadLocationException {

        String oldText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText =
                oldText.substring(0, offset) +
                (string == null ? "" : string) +
                oldText.substring(offset + length);

        if (getByteLength(newText) <= maxBytes) {
            super.replace(fb, offset, length, string, attr);
        }
    }
}
