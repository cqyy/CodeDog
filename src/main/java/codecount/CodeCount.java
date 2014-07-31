package codecount;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by Administrator on 2014/7/30.
 */
public class CodeCount {

    public static CodeCounter codeCount(Path path) throws IOException {
        CodeCounter counter = new CodeCounter();
        JavaFileVisitor visitor = new JavaFileVisitor(counter);
        Files.walkFileTree(path, visitor);
        return counter;
    }

    private static class JavaFileVisitor implements FileVisitor<Path> {

        private CodeCounter counter ;
        public JavaFileVisitor(CodeCounter counter){
            this.counter = counter;
        }

        private void codeCount(Path path, CodeCounter codeCounter) throws IOException {
            final boolean[] inCommnet = {false};
            final String[] packageName = {""};
            CodeCounter.Counter counter = new CodeCounter.Counter();

            Files.lines(path).forEach((line) -> {
                line = line.trim();
                if (line.startsWith("package")) {
                    packageName[0] = line.substring("package".length(),line.length() -1);
                }
                if (line.length() == 0) {
                    counter.blanks++;
                } else if (inCommnet[0] || line.startsWith("/*") || line.startsWith("//")) {
                    counter.comments++;
                    if (line.endsWith("*/")) {
                        inCommnet[0] = false;
                    } else if (!line.startsWith("//")) {
                        inCommnet[0] = true;
                    }
                } else {
                    counter.codes++;
                }
            });
            codeCounter.addClazz(packageName[0],path.getFileName().toString(),counter);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.toString().endsWith(".java")){
                codeCount(file,counter);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            System.out.println(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }


    public static void main(String[] args) throws IOException {
        Path path = Paths.get("D:\\Documents\\GitHub\\datahub\\crawlerbolt\\");

        System.out.println( CodeCount.codeCount(path));
    }
}
