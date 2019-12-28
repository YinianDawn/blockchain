package core.bitcoin.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author dawn
 */
public class UnsafeByteArrayOutputStream extends ByteArrayOutputStream {

    public UnsafeByteArrayOutputStream() {
        super(32);
    }

    public UnsafeByteArrayOutputStream(int size) {
        super(size);
    }

    /**
     * Writes the specified byte to this byte array output stream.
     *
     * @param b the byte to be written.
     */
    @Override
    public void write(int b) {
        int newCount = count + 1;
        if (newCount > buf.length) {
            buf = copyOf(buf, Math.max(buf.length << 1, newCount));
        }
        buf[count] = (byte) b;
        count = newCount;
    }

    /**
     * Writes {@code len} bytes from the specified byte array
     * starting at offset {@code off} to this byte array output stream.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     */
    @Override
    public void write(byte[] b, int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        int newCount = count + len;
        if (newCount > buf.length) {
            buf = copyOf(buf, Math.max(buf.length << 1, newCount));
        }
        System.arraycopy(b, off, buf, count, len);
        count = newCount;
    }

    /**
     * Writes the complete contents of this byte array output stream to
     * the specified output stream argument, as if by calling the output
     * stream's write method using {@code out.write(buf, 0, count)}.
     *
     * @param out the output stream to which to write the data.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }

    /**
     * Resets the {@code count} field of this byte array output
     * stream to zero, so that all currently accumulated output in the
     * output stream is discarded. The output stream can be used again,
     * reusing the already allocated buffer space.
     *
     * @see java.io.ByteArrayInputStream#count
     */
    @Override
    public void reset() {
        count = 0;
    }

    /**
     * Creates a newly allocated byte array. Its size is the current
     * size of this output stream and the valid contents of the buffer
     * have been copied into it.
     *
     * @return the current contents of this output stream, as a byte array.
     * @see java.io.ByteArrayOutputStream#size()
     */
    @Override
    public byte[] toByteArray() {
        return count == buf.length ? buf : copyOf(buf, count);
    }

    /**
     * Returns the current size of the buffer.
     *
     * @return the value of the {@code count} field, which is the number
     *         of valid bytes in this output stream.
     * @see java.io.ByteArrayOutputStream#count
     */
    @Override
    public int size() {
        return count;
    }

    private static byte[] copyOf(byte[] in, int length) {
        byte[] out = new byte[length];
        System.arraycopy(in, 0, out, 0, Math.min(length, in.length));
        return out;
    }
}
