# roop-utils
A collection of useful Java util classes.

1. Binery Utils <br/>
Info: Provides a Enum for simple and fast convertion between byte arrays and hex, base64 or utf8 String. <br/>
Warning: Does not work under Android. <br/>
Usage: <br/>
		byte[] data = ByteUtil.Base64.getBytes("SGFsbG8gV2VsdA=="); <br/>
		String str  = ByteUtil.Base64.getString(data); <br />

2. Hash Utils <br/>
Info: Provides a Enum for simple and fast hashing of a byte array to CRC32, SHA-1, SHA-256 and SHA-512. <br/>
Warning: no <br/>
Usage: <br/>
		byte[] data = HashUtil.SHA256.hash(ByteUtil.UTF8.getBytes("Hallo Welt")); <br/>
		byte[] data = HashUtil.SHA256.hash(new File("/test.file"); <br/>

3. File Utils <br/>
Info: Provides a Tool to Read / Write files in parts. The size of those parts is set via constructor parameters. For fileaccess an instance of RandomAccessFile is used which allows access to files greater than 4 GiB. <br/>
Warning: no <br/>
Usage: <br/>
		//constructor with filepath, writeable and partsize 						<br/>
		PartExtractor ext = new PartExtractor("/test.txt", false, 2*1024*1024);		<br/>
		//useful infos																<br/>
		ext.getLength();															<br/>
		ext.partSize(); 															<br/>
		ext.partCount(); //returns number of parts for this file					<br/>
		//read data																	<br/>
		FilePart tmp = new FilePart(0); //Create new FilePart with number 0			<br/>
		ext.fill(tmp); //reads part number 0 from file into FilePart				<br/>
		byte[] data = tmp.getData();												<br/>
		tmp.setNumber(3);															<br/>
		ext.fill(tmp); //reads part number 3 from file into FilePart				<br/>
		//write data																<br/>
		ext.write(tmp): //writes data from FilePart into third position				<br/>
		