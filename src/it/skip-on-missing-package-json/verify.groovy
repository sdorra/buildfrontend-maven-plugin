def logFile = new File( basedir, 'build.log' )
assert logFile.exists()
content = logFile.text

assert content.contains( 'execution skipped, because no package.json found' )
