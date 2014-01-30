package org.gbif.common.parsers;

import org.gbif.api.model.checklistbank.ParsedName;
import org.gbif.common.parsers.core.Parsable;
import org.gbif.common.parsers.core.ParseResult;
import org.gbif.nameparser.NameParser;
import org.gbif.nameparser.UnparsableException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton implementation using regex to extract a scientific name after a typestatus from a string.
 * For example given the input "Holotype of Dianthus fruticosus ssp. amorginus Runemark"
 * the parser will extract Dianthus fruticosus ssp. amorginus Runemark.
 */
public class TypifiedNameParser implements Parsable<String> {
  protected final Logger log = LoggerFactory.getLogger(getClass());
  private static TypifiedNameParser singletonObject = null;

  private static final Range<Integer> REASONABLE_NAME_SIZE_RANGE = Range.closed(4, 40);
  private static final NameParser NAME_PARSER = new NameParser();
  private static final Pattern NAME_SEPARATOR = Pattern.compile("\\sOF\\W*\\s+\\W*(.+)\\W*\\s*$", Pattern.CASE_INSENSITIVE);
  private static final Pattern CLEAN_WHITESPACE = Pattern.compile("\\s+");

  private TypifiedNameParser() {
  }

  @Override
  public ParseResult<String> parse(String input) {
    if (!Strings.isNullOrEmpty(input)) {
      Matcher m = NAME_SEPARATOR.matcher(input);
      if (m.find()) {
        String name = m.group(1);
        try {
          ParsedName pn = NAME_PARSER.parse(name);
          return ParseResult.success(ParseResult.CONFIDENCE.PROBABLE, pn.canonicalNameComplete());

        } catch (UnparsableException e) {
          log.debug("Cannot parse typified name: [{}] from input [{}]", name, input);
          name = CLEAN_WHITESPACE.matcher(name).replaceAll(" ").trim();
          if (REASONABLE_NAME_SIZE_RANGE.contains(name.length())) {
            return ParseResult.success(ParseResult.CONFIDENCE.POSSIBLE, name);
          }
        }
      }
    }
    return ParseResult.fail();
  }

  public static TypifiedNameParser getInstance() {
    synchronized (TypifiedNameParser.class) {
      if (singletonObject == null) {
        singletonObject = new TypifiedNameParser();
      }
    }
    return singletonObject;
  }


}
