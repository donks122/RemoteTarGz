package remote.tar.gz.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class SizeLimitInputStream extends InputStream {
	final long maxSize;
	final InputStream base;
	long alreadyRead;

	public SizeLimitInputStream(InputStream in, long maxSize) {
		super();
		this.maxSize = maxSize;
		alreadyRead = 0;
		base = in;
	}

	@Override
	public synchronized int available() throws IOException {		
		long a = base.available();
		if (alreadyRead + a > maxSize)
			a = maxSize - alreadyRead;
		return (int)a;			
	}

	@Override
	public void close() {
		// do nothing
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public void mark(int readlimit) {
		// do nothing
	}

	@Override
	public void reset() throws IOException {
		// do nothing 
	}

	@Override
	public synchronized int read() throws IOException {
		if (alreadyRead >= maxSize)
			throw new EOFException();
		int r = base.read();
		alreadyRead += 1;
		return r;
	}

	@Override
	public synchronized int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public synchronized int read(byte[] b, int off, int len) throws IOException {
		if (alreadyRead >= maxSize)
			return -1;
		if (alreadyRead + len > maxSize)
			len = (int)(maxSize - alreadyRead);
		int r = base.read(b, off, len);
		alreadyRead += r;
		return r;
	}

	@Override
	public synchronized long skip(long n) throws IOException {
		if (n < 0)
			return 0;
		if (alreadyRead >= maxSize)
			return 0;
		if (alreadyRead + n > maxSize)
			n = maxSize - alreadyRead;
		long r = base.skip(n);
		alreadyRead += r;
		return r;
	}



}
