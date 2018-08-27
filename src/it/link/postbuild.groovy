File file = new File(basedir, "main/target/awesome.txt")
if (!file.exists()) {
    throw new IllegalStateException("file does not exists")
}

if (! "awesome".equals(file.text.trim())) {
    throw new IllegalStateException("file should contain awesome")
}
