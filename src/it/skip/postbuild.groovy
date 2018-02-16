File file = new File(basedir, "awesome.txt")
if (file.exists()) {
    throw new IllegalStateException("script was executed, but should be skipped")
}