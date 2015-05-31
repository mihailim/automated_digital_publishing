/* Copyright (C) 2013-2015  Stephan Kreutzer
 *
 * This file is part of html2epub2.
 *
 * html2epub2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2epub2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2epub2. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/html2epub2.java
 * @brief The main module that delegates to the specific processors.
 * @author Stephan Kreutzer
 * @since 2015-04-11
 */



import java.util.ArrayList;
import java.io.File;
import java.util.ListIterator;



public class html2epub2
{
    public static void main(String args[])
    {
        System.out.print("html2epub2  Copyright (C) 2013-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");

        if (args.length != 1)
        {
            System.out.print("Usage:\n" +
                             "\thtml2epub2 config-file\n\n");
            System.exit(1);
        }


        System.out.println("html2epub2: Reading configuration.");

        ConfigProcessor configuration = new ConfigProcessor(args[0]);
        configuration.run();

        ArrayList<File> xhtmlInFiles = configuration.GetInFiles();
        ArrayList<String> xhtmlInFileTitles = configuration.GetInFileTitles();

        if (configuration.GetXHTMLSchemaValidation() == true)
        {
            ListIterator<File> inFileIter = xhtmlInFiles.listIterator();
            
            while (inFileIter.hasNext())
            {
                File inFile = inFileIter.next();

                System.out.print("html2epub2: Validating '" + inFile.getAbsolutePath() + "'.\n");

                XHTMLValidator xhtmlValidator = new XHTMLValidator();
                xhtmlValidator.validate(inFile);
            }
        }

        File outDirectory = configuration.GetOutDirectory();
        boolean xhtmlReaderDTDValidation = configuration.GetXHTMLReaderDTDValidation();
        boolean xhtmlReaderNamespaceProcessing = configuration.GetXHTMLReaderNamespaceProcessing();
        boolean xhtmlReaderCoalesceAdjacentCharacterData = configuration.GetXHTMLReaderCoalesceAdjacentCharacterData();
        boolean xhtmlReaderReplaceEntityReferences = configuration.GetXHTMLReaderReplaceEntityReferences();
        boolean xhtmlReaderResolveExternalParsedEntities = configuration.GetXHTMLReaderResolveExternalParsedEntities();
        boolean xhtmlReaderUseDTDNotDTDFallback = configuration.GetXHTMLReaderUseDTDNotDTDFallback();

        if (xhtmlReaderDTDValidation == false)
        {
            /**
             * @todo Without this adjustment, the XML reader would take very much time in an
             *     environment where DTD validation is not supported by the parser. It
             *     should be checked if there are no other reasons for time consumption than
             *     this inconsistency of settings. Only occurs if the XML file contains a
             *     DOCTYPE declaration.
             */
            xhtmlReaderUseDTDNotDTDFallback = false;
        }

        System.out.print("html2epub2: Setting up EPUB3.\n");
        
        EPUBSetup epubSetup = new EPUBSetup();
        epubSetup.run(outDirectory,
                      xhtmlInFiles,
                      xhtmlInFileTitles,
                      configuration.GetMetaData(),
                      xhtmlReaderDTDValidation,
                      xhtmlReaderNamespaceProcessing,
                      xhtmlReaderCoalesceAdjacentCharacterData,
                      xhtmlReaderReplaceEntityReferences,
                      xhtmlReaderResolveExternalParsedEntities,
                      xhtmlReaderUseDTDNotDTDFallback);

        XHTMLProcessor xhtmlProcessor = new XHTMLProcessor();
        
        xhtmlProcessor.generateNavTOC(xhtmlInFiles,
                                      xhtmlInFileTitles,
                                      new File(outDirectory.getAbsolutePath() + File.separator + "OPS" + File.separator + "navtoc.xhtml"),
                                      "Table of Contents");

        ArrayList<File> xhtmlOutFiles = new ArrayList<File>();
        ArrayList<File> imageOutFiles = new ArrayList<File>();

        for (int currentXHTMLFile = 1; currentXHTMLFile <= xhtmlInFiles.size(); currentXHTMLFile++)
        {
            File inFile = xhtmlInFiles.get(currentXHTMLFile-1);
            File outFile = new File(outDirectory.getAbsolutePath() + File.separator + "OPS" + File.separator + "page_" + currentXHTMLFile + ".xhtml");
            
            String title = xhtmlInFileTitles.get(currentXHTMLFile-1);

            System.out.print("html2epub2: Processing '" + inFile.getAbsolutePath() + "'.\n");
            
            xhtmlProcessor.processFile(inFile,
                                       outFile,
                                       title,
                                       xhtmlInFiles,
                                       epubSetup.GetReferencedImageFiles(),
                                       epubSetup.GetReferencedCSSFiles(),
                                       xhtmlReaderDTDValidation,
                                       xhtmlReaderNamespaceProcessing,
                                       xhtmlReaderCoalesceAdjacentCharacterData,
                                       xhtmlReaderReplaceEntityReferences,
                                       xhtmlReaderResolveExternalParsedEntities,
                                       xhtmlReaderUseDTDNotDTDFallback);

            xhtmlOutFiles.add(outFile);
        }
        
        System.out.print("html2epub2: Packing to EPUB3.\n");
        
        ArrayList<File> packageFiles = new ArrayList<File>();
        
        /**
         * @todo This should come from a getter of EPUBSetup.
         */
        packageFiles.add(new File(outDirectory.getAbsolutePath() + File.separator + "OPS" + File.separator + "package.opf"));
        packageFiles.add(new File(outDirectory.getAbsolutePath() + File.separator + "OPS" + File.separator + "toc.ncx"));
        packageFiles.add(new File(outDirectory.getAbsolutePath() + File.separator + "OPS" + File.separator + "navtoc.xhtml"));

        // Note that it is actually possible to have one single XHTML file
        // configured multiple times as input, so it can be re-used, while
        // referenced image files will always be present only once.
        packageFiles.addAll(xhtmlOutFiles);
        packageFiles.addAll(epubSetup.GetImageOutFiles());
        packageFiles.addAll(epubSetup.GetCSSOutFiles());
        
        ZipProcessor zipProcessor = new ZipProcessor();
        zipProcessor.Run(packageFiles, outDirectory);

        System.exit(0);
    }
}
