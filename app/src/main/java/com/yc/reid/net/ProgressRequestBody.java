package com.yc.reid.net;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * @Author nike
 * @Date 2023/7/3 16:06
 * @Description
 */
public class ProgressRequestBody extends RequestBody {
    private final RequestBody delegate;
    private final ProgressListener listener;

    public ProgressRequestBody(RequestBody requestBody, ProgressListener listener) {
        this.delegate = requestBody;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink = Okio.buffer(countingSink(sink));
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink countingSink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0;
            long contentLength = 0;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                bytesWritten += byteCount;
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                listener.onProgress(bytesWritten, contentLength);
            }
        };
    }

    public interface ProgressListener {
        void onProgress(long bytesWritten, long contentLength);
    }
}
