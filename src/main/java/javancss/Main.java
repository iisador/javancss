/*
Copyright (C) 2014 Chr. Clemens Lee <clemens@kclee.com>.

This file is part of JavaNCSS
(http://javancss.codehaus.org/).

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA*/

package javancss;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Main class of the JavaNCSS application. It does nothing than starting the batch process and immediately delegates control to the Javancss class.
 *
 * @author Chr. Clemens Lee <clemens@kclee.com>
 * @version $Id$
 */
public class Main {

    private static Map<String, Path> imageTemplates = new HashMap<>();
    private static Map<String, Path> htmlTemplates = new HashMap<>();
    private static Map<String, Path> projects = new HashMap<>();

    static {
        imageTemplates.put("ccn.svg", Paths.get("D:\\workspace\\javancss\\xslt\\svg\\chart_ccn.xsl"));
        imageTemplates.put("function_package.svg", Paths.get("D:\\workspace\\javancss\\xslt\\svg\\chart_function_package.xsl"));
        imageTemplates.put("ncss.svg", Paths.get("D:\\workspace\\javancss\\xslt\\svg\\chart_ncss.xsl"));
        imageTemplates.put("ncss_package.svg", Paths.get("D:\\workspace\\javancss\\xslt\\svg\\chart_ncss_package.xsl"));
    }

    static {
        htmlTemplates.put("document11.html", Paths.get("D:\\workspace\\javancss\\xslt\\javancss2document11.xsl"));
        htmlTemplates.put("html.html", Paths.get("D:\\workspace\\javancss\\xslt\\javancss2html.xsl"));
        htmlTemplates.put("methodhtml.html", Paths.get("D:\\workspace\\javancss\\xslt\\javancss2methodhtml.xsl"));
        htmlTemplates.put("text.txt", Paths.get("D:\\workspace\\javancss\\xslt\\javancss2text.xsl"));
        htmlTemplates.put("graph.html", Paths.get("D:\\workspace\\javancss\\xslt\\svg\\javancss_graph.xsl"));
    }

    static {
        projects.put("fns.exchange", Paths.get("D:\\workspace\\ppod\\fns\\fns.exchange"));
        projects.put("fns.web", Paths.get("D:\\workspace\\ppod\\fns\\fns.web"));
    }


    public static void main(String[] args) throws IOException, TransformerException {
        Files.walkFileTree(Paths.get("D:\\workspace\\javancss\\gen"), new Walker());

        //        Locale.setDefault(Locale.US);
        //
        //        Javancss javancss = new Javancss(args);
        //
        //        if (javancss.getLastErrorMessage() != null) {
        //            System.exit(1);
        //        }
    }

    private static void transform(Path xml, Path root, Map<String, Path> templates) throws IOException, TransformerException {
        for (Map.Entry<String, Path> e : templates.entrySet()) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(Files.newInputStream(e.getValue())));
            transformer.transform(new StreamSource(Files.newInputStream(xml)), new StreamResult(Files.newOutputStream(root.resolve(e.getKey()),
                StandardOpenOption.CREATE)));
        }
    }

    private static class Walker extends SimpleFileVisitor<Path> {

        private Path modulePath;

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            modulePath = dir;
            return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.getFileName().toString().equals("src.txt")) {
                Path xmlPath = modulePath.resolve("out.xml");
                new Javancss(new String[]{"-all", "-xml", "-out", xmlPath.toString(), "@" + file});

                Files.createDirectories(modulePath.resolve("img"));
                try {
                    transform(xmlPath, modulePath.resolve("img"), imageTemplates);
                } catch (TransformerException e) {
                    throw new RuntimeException(e);
                }

                try {
                    transform(xmlPath, modulePath, htmlTemplates);
                } catch (TransformerException e) {
                    throw new RuntimeException(e);
                }
            }
            return super.visitFile(file, attrs);
        }
    }
}
