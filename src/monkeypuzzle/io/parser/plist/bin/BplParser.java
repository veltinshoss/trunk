package monkeypuzzle.io.parser.plist.bin;

/*
 * @(#)BplParser.java  1.0  2005-11-06
 *
 * FIXME We need to check this license before we do anything commercial
 *
 * Copyright (c) 2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Werner Randelshofer. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Werner Randelshofer.
 * 
 * Further Edited by other than the author
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

import monkeypuzzle.io.parser.plist.PListArray;
import monkeypuzzle.io.parser.plist.PListContainer;
import monkeypuzzle.io.parser.plist.PListDict;
import monkeypuzzle.io.parser.plist.PListHeader;
import monkeypuzzle.io.util.IphoneDate;
import net.n3.nanoxml.XMLElement;

/**
 * Reads a binary PList file and returns it as a NanoXML XMLElement.
 * <p/>
 * The NanoXML XMLElement returned by this reader is equivalent to the
 * XMLElement returned, if a PList file in XML format is parsed with NanoXML.
 * <p/>
 * Description about property list taken from <a href="http://developer.apple.com/documentation/Cocoa/Conceptual/PropertyLists/index.html#//apple_ref/doc/uid/10000048i"
 * > Apple's online documentation</a>:
 * <p/>"A property list is a data representation used by Mac OS X Cocoa and Core
 * Foundation as a convenient way to store, organize, and access standard object
 * types. Frequently called a plist, a property list is an object of one of
 * several certain Cocoa or Core Foundation types, including arrays,
 * dictionaries, strings, binary data, numbers, dates, and Boolean values. If
 * the object is a container (an array or dictionary), all objects contained
 * within it must also be supported property list objects. (Arrays and
 * dictionaries can contain objects not supported by the architecture, but are
 * then not property lists, and cannot be saved and restored with the various
 * property list methods.)"
 * <p/>
 * 
 * @author Werner Randelshofer
 * @version 0.1 June 18, 2005 Created.
 * @see XMLElement
 */
public class BplParser implements PListHeader
{
	public static boolean checkHeader(final ByteBuffer buffer)
	{
		// Parse the HEADER
		// ----------------
		// magic number ("bplist")
		// file format version ("00")
		int bpli = buffer.getInt();
		int st00 = buffer.getInt();
		return ((bpli == 0x62706c69) && (st00 == 0x73743030));
	}

	/*
	 * Description of the binary plist format derived from
	 * http://cvs.opendarwin.
	 * org/cgi-bin/cvsweb.cgi/~checkout~/src/CoreFoundation
	 * /Parsing.subproj/CFBinaryPList.c?rev=1.1.1.3&content-type=text/plain
	 * 
	 * EBNF description of the file format: <pre> bplist ::= header objectTable
	 * offsetTable trailer
	 * 
	 * header ::= magicNumber fileFormatVersion magicNumber ::= "bplist"
	 * fileFormatVersion ::= "00"
	 * 
	 * objectTable ::= { null | bool | fill | number | date | data | string |
	 * uid | array | dict }
	 * 
	 * null ::= 0b0000 0b0000
	 * 
	 * bool ::= false | true false ::= 0b0000 0b1000 true ::= 0b0000 0b1001
	 * 
	 * fill ::= 0b0000 0b1111 // fill byte
	 * 
	 * number ::= int | real int ::= 0b0001 0bnnnn byte(2^nnnn) // 2^nnnn
	 * big-endian bytes real ::= 0b0010 0bnnnn byte(2^nnnn) // 2^nnnn big-endian
	 * bytes
	 * 
	 * date ::= 0b0011 0b0011 byte8 // 8 byte float big-endian bytes
	 * 
	 * data ::= 0b0100 0bnnnn [int] byte // nnnn is number of bytes // unless
	 * 0b1111 then a int // variable-sized object follows // to indicate the
	 * number of bytes
	 * 
	 * string ::= asciiString | unicodeString asciiString ::= 0b0101 0bnnnn
	 * [int] byte unicodeString ::= 0b0110 0bnnnn [int] short // nnnn is number
	 * of bytes // unless 0b1111 then a int // variable-sized object follows //
	 * to indicate the number of bytes
	 * 
	 * uid ::= 0b1000 0bnnnn byte // nnnn+1 is # of bytes
	 * 
	 * array ::= 0b1010 0bnnnn [int] objref // // nnnn is number of objref //
	 * unless 0b1111 then a int // variable-sized object follows // to indicate
	 * the number of objref
	 * 
	 * dict ::= 0b1010 0bnnnn [int] keyref objref // nnnn is number of keyref
	 * and // objref pairs // unless 0b1111 then a int // variable-sized object
	 * follows // to indicate the number of pairs
	 * 
	 * objref = byte | short // if refCount // is less than 256 then objref is
	 * // an unsigned byte, otherwise it // is an unsigned big-endian short
	 * 
	 * keyref = byte | short // if refCount // is less than 256 then objref is
	 * // an unsigned byte, otherwise it // is an unsigned big-endian short
	 * 
	 * unused ::= 0b0111 0bxxxx | 0b1001 0bxxxx | 0b1011 0bxxxx | 0b1100 0bxxxx
	 * | 0b1110 0bxxxx | 0b1111 0bxxxx
	 * 
	 * 
	 * offsetTable ::= { int } // list of ints, byte size of which // is given
	 * in trailer // these are the byte offsets into // the file // number of
	 * these is in the trailer
	 * 
	 * trailer ::= refCount offsetCount objectCount topLevelOffset
	 * 
	 * refCount ::= byte8 // unsigned big-endian long offsetCount ::= byte8 //
	 * unsigned big-endian long objectCount ::= byte8 // unsigned big-endian
	 * long topLevelOffset ::= byte8 // unsigned big-endian long </pre>
	 */
	@SuppressWarnings("unused")
	private int objectCount;
	/**
	 * Object table. We gradually fill in objects from the binary PList object
	 * table into this list.
	 */
	private ArrayList<PListContainer> objectTable;
	@SuppressWarnings("unused")
	private int offsetCount;
	/**
	 * Total count of objrefs and keyrefs.
	 */
	private int refCount;

	/**
	 * Offset in file of top level offset in offset table.
	 */
	private int topLevelOffset;

	/**
	 * Creates a new instance.
	 */
	public BplParser(final InputStream is) throws IOException
	{
		parse(is);
	}

	/**
	 * Parses a binary PList file and turns it into a XMLElement. The XMLElement
	 * is equivalent with a XML PList file parsed using NanoXML.
	 * 
	 * @param file
	 *            A file containing a binary PList.
	 * @return Returns the parsed XMLElement.
	 */
	public XMLElement getAsXml() throws IOException
	{
		// Convert the object table to XML and return it
		XMLElement root = new XMLElement();
		root.setName("plist");
		root.setAttribute("version", "1.0");
		root.addChild(this.objectTable.get(0).toXml());
		return root;
	}

	public PListContainer getRootContainer()
	{
		return this.objectTable.get(0);
	}

	/**
	 * Parses a binary PList file
	 * 
	 * @param file
	 *            A file containing a binary PList.
	 */
	private void parse(final InputStream is) throws FileNotFoundException,
			IOException
	{
		ReadableByteChannel c = Channels.newChannel(is);
		ByteBuffer buffer = ByteBuffer.allocate(is.available());
		c.read(buffer);
		buffer.clear();
		if (!checkHeader(buffer))
			throw new IOException(
					"parseHeader: File does not start with 'bplist00' magic.");
		;
		// Parse the TRAILER
		// ----------------
		// byte size of offset ints in offset table
		// byte size of object refs in arrays and dicts
		// number of offsets in offset table (also is number of objects)
		// element # in offset table which is top level object
		buffer.position(buffer.capacity() - 32);
		// count of offset ints in offset table
		this.offsetCount = (int) buffer.getLong();
		// count of object refs in arrays and dicts
		this.refCount = (int) buffer.getLong();
		// count of offsets in offset table (also is number of objects)
		this.objectCount = (int) buffer.getLong();
		// element # in offset table which is top level object
		this.topLevelOffset = (int) buffer.getLong();
		buffer.clear();
		buffer.position(8);
		// Parse the OBJECT TABLE
		// ----------------------
		this.objectTable = new ArrayList<PListContainer>();
		parseObjectTable(buffer);
	}

	/**
	 * string 0101 nnnn [int] ... // ASCII string, nnnn is # of chars, else 1111
	 * then int count, then bytes
	 */
	private void parseAsciiString(final ByteBuffer in, final int count)
			throws IOException
	{
		byte[] buf = new byte[count];
		in.get(buf);
		String str = new String(buf, "ASCII");
		this.objectTable.add(new BplPrimitive(str));
	}

	/**
	 * array 1010 nnnn [int] objref* // nnnn is count, unless '1111', then int
	 * count follows
	 */
	private void parseByteArray(final ByteBuffer in, final int count)
			throws IOException
	{
		int[] objref = new int[count];
		for (int i = 0; i < count; i++)
		{
			objref[i] = (in.get() & 0xff);
		}
		PListArray arr = new BplArray(this.objectTable, objref);
		this.objectTable.add(arr);
	}

	/**
	 * byte dict 1101 nnnn keyref* objref* // nnnn is less than '1111'
	 */
	private void parseByteDict(final ByteBuffer in, final int count)
			throws IOException
	{
		int[] keyref = new int[count];
		int[] objref = new int[count];
		for (int i = 0; i < count; i++)
		{
			keyref[i] = (in.get() & 0xff);
		}
		for (int i = 0; i < count; i++)
		{
			objref[i] = (in.get() & 0xff);
		}
		PListDict dict = new BplDict(this.objectTable, keyref, objref);
		this.objectTable.add(dict);
	}

	/*
	 * data 0100 nnnn [int] ... // nnnn is number of bytes unless 1111 then int
	 * count follows, followed by bytes
	 */
	private void parseData(final ByteBuffer in, final int count)
			throws IOException
	{
		byte[] data = new byte[count];
		in.get(data);
		this.objectTable.add(new BplPrimitive(data));
	}

	/**
	 * date 0011 0011 ... // 8 byte float follows, big-endian bytes
	 */
	private void parseDate(final ByteBuffer in) throws IOException
	{
		double date = in.getDouble();
		this.objectTable.add(new BplPrimitive(IphoneDate
				.iphoneToDate((long) date)));
	}

	/**
	 * int 0001 nnnn ... // # of bytes is 2^nnnn, big-endian bytes
	 */
	private void parseInt(final ByteBuffer in, final int count)
			throws IOException
	{
		if (count > 8)
			throw new IOException("parseInt: unsupported byte count:" + count);
		long value = 0;
		for (int i = 0; i < count; i++)
		{
			int b = (in.get() & 0xff);
			value = (value << 8) | b;
		}
		this.objectTable.add(new BplPrimitive(value));
	}

	/**
	 * Object Formats (marker byte followed by additional info in some cases)
	 * null 0000 0000 bool 0000 1000 // false bool 0000 1001 // true fill 0000
	 * 1111 // fill byte int 0001 nnnn ... // # of bytes is 2^nnnn, big-endian
	 * bytes real 0010 nnnn ... // # of bytes is 2^nnnn, big-endian bytes date
	 * 0011 0011 ... // 8 byte float follows, big-endian bytes data 0100 nnnn
	 * [int] ... // nnnn is number of bytes unless 1111 then int count follows,
	 * followed by bytes string 0101 nnnn [int] ... // ASCII string, nnnn is #
	 * of chars, else 1111 then int count, then bytes string 0110 nnnn [int] ...
	 * // Unicode string, nnnn is # of chars, else 1111 then int count, then
	 * big-endian 2-byte shorts 0111 xxxx // unused uid 1000 nnnn ... // nnnn+1
	 * is # of bytes 1001 xxxx // unused array 1010 nnnn [int] objref* // nnnn
	 * is count, unless '1111', then int count follows 1011 xxxx // unused 1100
	 * xxxx // unused dict 1101 nnnn [int] keyref* objref* // nnnn is count,
	 * unless '1111', then int count follows 1110 xxxx // unused 1111 xxxx //
	 * unused
	 */
	private void parseObjectTable(final ByteBuffer in) throws IOException
	{
		while (in.position() <= this.topLevelOffset)
		{
			int marker = (in.get() & 0xff);
			switch ((marker & 0xf0) >> 4)
			{
			case 0:
				{
					parsePrimitive(in, marker & 0xf);
					break;
				}
			case 1:
				{
					int count = 1 << (marker & 0xf);
					parseInt(in, count);
					break;
				}
			case 2:
				{
					int count = 1 << (marker & 0xf);
					parseReal(in, count);
					break;
				}
			case 3:
				{
					if ((marker & 0xf) != 3)
						throw new IOException(
								"parseObjectTable: illegal marker "
										+ Integer.toBinaryString(marker));
					parseDate(in);
					break;
				}
			case 4:
				{
					int count = marker & 0xf;
					if (count == 15)
					{
						count = readCount(in);
					}
					parseData(in, count);
					break;
				}
			case 5:
				{
					int count = marker & 0xf;
					if (count == 15)
					{
						count = readCount(in);
					}
					parseAsciiString(in, count);
					break;
				}
			case 6:
				{
					int count = marker & 0xf;
					if (count == 15)
					{
						count = readCount(in);
					}
					parseUnicodeString(in, count);
					break;
				}
			case 7:
				{
					return;
					// throw new IOException("parseObjectTable: illegal marker
					// "+Integer.toBinaryString(marker));
					// break;
				}
			case 8:
				{

					int count = 1 << (marker & 0xf);
					parseInt(in, count);

					// // FIXME - Option 8
					// int count = (marker & 0xf) + 1;
					// byte[] buf = new byte[count];
					// in.get(buf);
					// System.out.println("Ignored: " + count);
					// objectTable.add(new BplPrimitive("XYZ"));
					break;
				}
			case 9:
				{
					throw new IOException("parseObjectTable: illegal marker "
							+ Integer.toBinaryString(marker));
					// break;
				}
			case 10:
				{
					int count = marker & 0xf;
					if (count == 15)
					{
						count = readCount(in);
					}
					if (this.refCount > 255)
					{
						parseShortArray(in, count);
					} else
					{
						parseByteArray(in, count);
					}
					break;
				}
			case 11:
				{
					throw new IOException("parseObjectTable: illegal marker "
							+ Integer.toBinaryString(marker));
					// break;
				}
			case 12:
				{
					throw new IOException("parseObjectTable: illegal marker "
							+ Integer.toBinaryString(marker));
					// break;
				}
			case 13:
				{
					int count = marker & 0xf;
					if (count == 15)
					{
						count = readCount(in);
					}
					if (this.refCount > 256)
					{
						parseShortDict(in, count);
					} else
					{
						parseByteDict(in, count);
					}
					break;
				}
			case 14:
				{
					throw new IOException("parseObjectTable: illegal marker "
							+ Integer.toBinaryString(marker));
					// break;
				}
			case 15:
				{
					throw new IOException("parseObjectTable: illegal marker "
							+ Integer.toBinaryString(marker));
					// break;
				}
			}
		}
	}

	/**
	 * null 0000 0000 bool 0000 1000 // false bool 0000 1001 // true fill 0000
	 * 1111 // fill byte
	 */
	private void parsePrimitive(final ByteBuffer in, final int primitive)
			throws IOException
	{
		switch (primitive)
		{
		case 0:
			this.objectTable.add(new BplPrimitive(null));
			break;
		case 8:
			this.objectTable.add(new BplPrimitive(Boolean.FALSE));
			break;
		case 9:
			this.objectTable.add(new BplPrimitive(Boolean.TRUE));
			break;
		case 15:
			// fill byte: don't add to object table
			break;
		default:
			throw new IOException("parsePrimitive: illegal primitive "
					+ Integer.toBinaryString(primitive));
		}
	}

	/**
	 * real 0010 nnnn ... // # of bytes is 2^nnnn, big-endian bytes
	 */
	private void parseReal(final ByteBuffer in, final int count)
			throws IOException
	{
		switch (count)
		{
		case 4:
			this.objectTable.add(new BplPrimitive(in.getFloat()));
			break;
		case 8:
			this.objectTable.add(new BplPrimitive(in.getDouble()));
			break;
		default:
			throw new IOException("parseReal: unsupported byte count:" + count);
		}
	}

	/**
	 * array 1010 nnnn [int] objref* // nnnn is count, unless '1111', then int
	 * count follows
	 */
	private void parseShortArray(final ByteBuffer in, final int count)
			throws IOException
	{
		int[] objref = new int[count];
		for (int i = 0; i < count; i++)
		{
			objref[i] = in.getShort() & 0xffff;
		}
		PListArray arr = new BplArray(this.objectTable, objref);
		this.objectTable.add(arr);
	}

	/**
	 * short dict 1101 ffff int keyref* objref* // int is count
	 */
	private void parseShortDict(final ByteBuffer in, final int count)
			throws IOException
	{
		int[] keyref = new int[count];
		int[] objref = new int[count];
		for (int i = 0; i < count; i++)
		{
			keyref[i] = in.getShort() & 0xffff;
		}
		for (int i = 0; i < count; i++)
		{
			objref[i] = in.getShort() & 0xffff;
		}
		PListDict dict = new BplDict(this.objectTable, keyref, objref);
		this.objectTable.add(dict);
	}

	/**
	 * string 0110 nnnn [int] ... // Unicode string, nnnn is # of chars, else
	 * 1111 then int count, then big-endian 2-byte shorts
	 */
	private void parseUnicodeString(final ByteBuffer in, final int count)
			throws IOException
	{
		char[] buf = new char[count];
		for (int i = 0; i < count; i++)
		{
			buf[i] = in.getChar();
		}
		String str = new String(buf);
		this.objectTable.add(new BplPrimitive(str));
	}

	/**
	 * Reads a count value from the object table. Count values are encoded using
	 * the following scheme:
	 * <p/>
	 * int 0001 nnnn ... // # of bytes is 2^nnnn, big-endian bytes
	 */
	private int readCount(final ByteBuffer in) throws IOException
	{
		int marker = (in.get() & 0xff);
		if (((marker & 0xf0) >> 4) != 1)
			throw new IOException("variableLengthInt: Illegal marker "
					+ Integer.toBinaryString(marker));
		int count = 1 << (marker & 0xf);
		int value = 0;
		for (int i = 0; i < count; i++)
		{
			int b = (in.get() & 0xff);
			value = (value << 8) | b;
		}
		return value;
	}
}
