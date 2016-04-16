# roop-utils
A collection of usefull Java util classes.

1. Binery Utils <br/>
Info: Provides a Enum for simple and fast convertion between byte arrays and hex, base64 or utf8 String. <br/>
Warning: Does not work under Android. <br/>
Usage: <br/>
	<t/>	byte[] data = ByteUtil.Base64.getBytes("SGFsbG8gV2VsdA=="); <br/>
	<t/>	String str  = ByteUtil.Base64.getString(data); <br />

2. Hash Utils <br/>
Info: Provides a Enum for simple and fast hashing of a byte array to CRC32, SHA-1, SHA-256 and SHA-512. <br/>
Warning: no <br/>
Usage: <br/>
		byte[] data = HashUtil.SHA256.hash(ByteUtil.UTF8.getBytes("Hallo Welt")); <br/>
		byte[] data = HashUtil.SHA256.hash(new File("/test.file"); <br/>