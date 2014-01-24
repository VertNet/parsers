package org.gbif.common.parsers.basisofrecord;

import org.gbif.common.parsers.FileBasedDictionaryParser;

import java.io.InputStream;

/**
 * Singleton implementation of the dictionary that uses the file /dictionaries/parse/basisOfRecord.txt.
 */
public class BasisOfRecordParser extends FileBasedDictionaryParser {

  private static BasisOfRecordParser singletonObject = null;

  private BasisOfRecordParser(boolean caseSensitive, InputStream... file) {
    super(caseSensitive, file);
  }

  public static BasisOfRecordParser getInstance()
    throws ClassCastException, AbstractMethodError, ArithmeticException, ArrayIndexOutOfBoundsException {
    synchronized (BasisOfRecordParser.class) {
      if (singletonObject == null) {
        singletonObject = new BasisOfRecordParser(false,
          BasisOfRecordParser.class.getResourceAsStream("/dictionaries/parse/basisOfRecord.txt"));
      }
    }
    return singletonObject;
  }
}
