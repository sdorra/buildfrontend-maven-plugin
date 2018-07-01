def logFile = new File( basedir, 'build.log' )
assert logFile.exists()
content = logFile.text

assert content.contains( 'package.json is missing' )
