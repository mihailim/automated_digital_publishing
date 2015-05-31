/* Copyright (C) 2014  Stephan Kreutzer
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
 * @file $/ZipProcessor.java
 * @brief Processor to pack (zip) the EPUB2 file.
 * @author Stephan Kreutzer
 * @since 2014-01-05
 */



import java.util.ArrayList;
import java.io.File;
import java.util.zip.ZipOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.zip.Checksum;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;



class ZipProcessor
{
    public ZipProcessor()
    {
    
    }

    public void Run(ArrayList<File> packageFiles, File outDirectory)
    {
        try
        {
            FileOutputStream outStream = new FileOutputStream(outDirectory.getAbsolutePath() + File.separator + "out.epub");
            ZipOutputStream zipStream = new ZipOutputStream(outStream);
            
            {
                File mimetypeFile = new File(outDirectory.getAbsolutePath() + File.separator + "mimetype");
                FileInputStream inStream = new FileInputStream(mimetypeFile);
                String mimetype = new String("application/epub+zip");

                long mimetypeFileSize = mimetypeFile.length();
                
                if (mimetypeFileSize != new String(mimetype).length())
                {
                    System.out.print("html2epub2: '" + mimetypeFile.getAbsolutePath() + "' has an invalid file size of " + mimetypeFileSize + ", expected is " + mimetype.length() + ".\n");
                    System.exit(-64);
                }
                
                if (mimetypeFileSize > 1024)
                {
                    System.out.print("html2epub2: Buffer size for mimetype file of " + mimetypeFileSize + " won't be sufficient.\n");
                    System.exit(-65);
                }


                byte[] buffer = new byte[1024];
                int length = 0;
                
                if ((length = inStream.read(buffer)) > 0)
                {
                    Checksum checksum = new CRC32();
                    // Calculate CRC32 checksum.
                    checksum.update(buffer, 0, length);
                    long crcChecksum = checksum.getValue();
                
                    ZipEntry zipEntry = new ZipEntry("mimetype");
                    
                    zipEntry.setMethod(ZipEntry.STORED);
                    zipEntry.setSize(length);
                    zipEntry.setCompressedSize(length);
                    zipEntry.setCrc(crcChecksum);
                        
                    zipStream.putNextEntry(zipEntry);
                    zipStream.write(buffer, 0, length);
                    inStream.close();
                    zipStream.closeEntry();
                }
                else
                {
                    System.out.print("html2epub2: Problem with reading the mimetype file '" + mimetypeFile.getAbsolutePath() + "'.\n");
                    System.exit(-66);
                }
            }
                  
            /**
             * @todo This could be outsourced if the ZipProcessor would provide
             *     support for a package list consisting of source/destination.
             */
                         
            this.AddFile(zipStream,
                         "META-INF/container.xml",
                         new File(outDirectory.getAbsolutePath() + File.separator + "META-INF" + File.separator + "container.xml"));
                         /*
            this.AddFile(zipStream,
                         "OPS/package.opf",
                         new File(outDirectory.getAbsolutePath() + File.separator + "OPS" + File.separator + "package.opf"));
            this.AddFile(zipStream,
                         "OPS/toc.ncx",
                         new File(outDirectory.getAbsolutePath() + File.separator + "OPS" + File.separator + "toc.ncx"));
            this.AddFile(zipStream,
                         "OPS/navtoc.xhtml",
                         new File(outDirectory.getAbsolutePath() + File.separator + "OPS" + File.separator + "navtoc.xhtml"));*/

            for (int packageFile = 0; packageFile < packageFiles.size(); packageFile++)
            {
                File currentPackageFile = packageFiles.get(packageFile);
            
                this.AddFile(zipStream,
                             "OPS/" + currentPackageFile.getName(),
                             currentPackageFile);
            }
       
            zipStream.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-67);
        }
    }

    private void AddFile(ZipOutputStream zipStream, String entryName, File file) throws IOException
    {
        ZipEntry zipEntry = new ZipEntry(entryName);
        zipStream.putNextEntry(zipEntry);
            
        FileInputStream inStream = new FileInputStream(file.getAbsolutePath());

        byte[] buffer = new byte[1024];
        int length = 0;
        
        while ((length = inStream.read(buffer)) > 0)
        {
            zipStream.write(buffer, 0, length);
        }

        inStream.close();
        zipStream.closeEntry();
    }
}
