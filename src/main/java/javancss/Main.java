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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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

    public static void main(String[] args) throws IOException, TransformerException {
        transform();
//        Locale.setDefault(Locale.US);
//
//        Javancss javancss = new Javancss(args);
//
//        if (javancss.getLastErrorMessage() != null) {
//            System.exit(1);
//        }
    }

    private static void transform() throws IOException, TransformerException {
        Path inPath = Paths.get("D:\\workspace\\javancss\\tst.xml");

        Map<String, Path> templates = new HashMap<>();
        templates.put("ccn.svg", Paths.get("D:\\workspace\\javancss\\xslt\\svg\\chart_ccn.xsl"));
        templates.put("function_package.svg", Paths.get("D:\\workspace\\javancss\\xslt\\svg\\chart_function_package.xsl"));
        templates.put("ncss.svg", Paths.get("D:\\workspace\\javancss\\xslt\\svg\\chart_ncss.xsl"));
        templates.put("ncss_package.svg", Paths.get("D:\\workspace\\javancss\\xslt\\svg\\chart_ncss_package.xsl"));
        templates.put("graph.html", Paths.get("D:\\workspace\\javancss\\xslt\\svg\\javancss_graph.xsl"));

        for (Map.Entry<String, Path> e: templates.entrySet()) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(Files.newInputStream(e.getValue())));
            transformer.transform(new StreamSource(Files.newInputStream(inPath)), new StreamResult(Files.newOutputStream(Paths.get("gen", "rufr_" + e.getKey()),
                StandardOpenOption.CREATE)));
        }
    }
}
