# compressions
通用文件压缩处理组件

## 快速开始
```java
/**
 * 压缩示例，目录下的文件全部压缩，不需要过滤
 */
Compressions.compress("src/test/resources/test.txt", "target/test.zip");


/**
 * 解压示例，全部解压，不需要过滤
 */
Compressions.uncompress("src/test/resources", "target/test.zip");

```

```java
/**
 * 压缩示例，按需求过滤
 */
Compressions.compress(new File(parent), new File(parent, "test.zip"), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.getFileName().toString().equals("testq")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().endsWith("zip")) {
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
```

```java
/**
 * 解压示例，按需求过滤
 */
Compressions.uncompress(new File(path), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(FileEntry dir, BasicFileAttributes attrs) throws IOException {
                    System.out.println("preVisitDirectory:" + dir.getPath());
                    File file = new File(parent, dir.getPath());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(FileEntry fileEntry, BasicFileAttributes attrs) throws IOException {
                    System.out.println("visitFile:" + fileEntry.getPath());
                    if (fileEntry.getPath().endsWith("jpg")){
                        File file = new File(parent, fileEntry.getPath());
                        fileEntry.transferTo(file);
                    }
                    return super.visitFile(fileEntry, attrs);
                }

                @Override
                public FileVisitResult visitFileFailed(FileEntry<?> file, IOException exc) throws IOException {
                    //读取出异常的压缩包全路径
                    String path2 = file.getPath();
                    return FileVisitResult.CONTINUE;
                }
            });
```