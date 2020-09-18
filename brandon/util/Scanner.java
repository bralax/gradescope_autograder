/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package brandon.util;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.regex.Pattern;
import java.util.regex.MatchResult;
import java.util.Locale;
import java.util.Iterator;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.nio.file.Path;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;
/**
 * A simple text scanner which can parse primitive types and strings using
 * regular expressions.
 *
 * <p>A <code>Scanner</code> breaks its input into tokens using a
 * delimiter pattern, which by default matches whitespace. The resulting
 * tokens may then be converted into values of different types using the
 * various <tt>next</tt> methods.
 *
 * <p>For example, this code allows a user to read a number from
 * <tt>System.in</tt>:
 * <blockquote><pre>{@code
 *     Scanner sc = new Scanner(System.in);
 *     int i = sc.nextInt();
 * }</pre></blockquote>
 *
 * <p>As another example, this code allows <code>long</code> types to be
 * assigned from entries in a file <code>myNumbers</code>:
 * <blockquote><pre>{@code
 *      Scanner sc = new Scanner(new File("myNumbers"));
 *      while (sc.hasNextLong()) {
 *          long aLong = sc.nextLong();
 *      }
 * }</pre></blockquote>
 *
 * <p>The scanner can also use delimiters other than whitespace. This
 * example reads several items in from a string:
 * <blockquote><pre>{@code
 *     String input = "1 fish 2 fish red fish blue fish";
 *     Scanner s = new Scanner(input).useDelimiter("\\s*fish\\s*");
 *     System.out.println(s.nextInt());
 *     System.out.println(s.nextInt());
 *     System.out.println(s.next());
 *     System.out.println(s.next());
 *     s.close();
 * }</pre></blockquote>
 * <p>
 * prints the following output:
 * <blockquote><pre>{@code
 *     1
 *     2
 *     red
 *     blue
 * }</pre></blockquote>
 *
 * <p>The same output can be generated with this code, which uses a regular
 * expression to parse all four tokens at once:
 * <blockquote><pre>{@code
 *     String input = "1 fish 2 fish red fish blue fish";
 *     Scanner s = new Scanner(input);
 *     s.findInLine("(\\d+) fish (\\d+) fish (\\w+) fish (\\w+)");
 *     MatchResult result = s.match();
 *     for (int i=1; i<=result.groupCount(); i++)
 *         System.out.println(result.group(i));
 *     s.close();
 * }</pre></blockquote>
 *
 * <p>The <a name="default-delimiter">default whitespace delimiter</a> used
 * by a scanner is as recognized by {@link java.lang.Character}.{@link
 * java.lang.Character#isWhitespace(char) isWhitespace}. The {@link #reset}
 * method will reset the value of the scanner's delimiter to the default
 * whitespace delimiter regardless of whether it was previously changed.
 *
 * <p>A scanning operation may block waiting for input.
 *
 * <p>The {@link #next} and {@link #hasNext} methods and their
 * primitive-type companion methods (such as {@link #nextInt} and
 * {@link #hasNextInt}) first skip any input that matches the delimiter
 * pattern, and then attempt to return the next token. Both <tt>hasNext</tt>
 * and <tt>next</tt> methods may block waiting for further input.  Whether a
 * <tt>hasNext</tt> method blocks has no connection to whether or not its
 * associated <tt>next</tt> method will block.
 *
 * <p> The {@link #findInLine}, {@link #findWithinHorizon}, and {@link #skip}
 * methods operate independently of the delimiter pattern. These methods will
 * attempt to match the specified pattern with no regard to delimiters in the
 * input and thus can be used in special circumstances where delimiters are
 * not relevant. These methods may block waiting for more input.
 *
 * <p>When a scanner throws an {@link InputMismatchException}, the scanner
 * will not pass the token that caused the exception, so that it may be
 * retrieved or skipped via some other method.
 *
 * <p>Depending upon the type of delimiting pattern, empty tokens may be
 * returned. For example, the pattern <tt>"\\s+"</tt> will return no empty
 * tokens since it matches multiple instances of the delimiter. The delimiting
 * pattern <tt>"\\s"</tt> could return empty tokens since it only passes one
 * space at a time.
 *
 * <p> A scanner can read text from any object which implements the {@link
 * java.lang.Readable} interface.  If an invocation of the underlying
 * readable's {@link java.lang.Readable#read} method throws an {@link
 * java.io.IOException} then the scanner assumes that the end of the input
 * has been reached.  The most recent <tt>IOException</tt> thrown by the
 * underlying readable can be retrieved via the {@link #ioException} method.
 *
 * <p>When a <code>Scanner</code> is closed, it will close its input source
 * if the source implements the {@link java.io.Closeable} interface.
 *
 * <p>A <code>Scanner</code> is not safe for multithreaded use without
 * external synchronization.
 *
 * <p>Unless otherwise mentioned, passing a <code>null</code> parameter into
 * any method of a <code>Scanner</code> will cause a
 * <code>NullPointerException</code> to be thrown.
 *
 * <p>A scanner will default to interpreting numbers as decimal unless a
 * different radix has been set by using the {@link #useRadix} method. The
 * {@link #reset} method will reset the value of the scanner's radix to
 * <code>10</code> regardless of whether it was previously changed.
 *
 * <h3> <a name="localized-numbers">Localized numbers</a> </h3>
 *
 * <p> An instance of this class is capable of scanning numbers in the standard
 * formats as well as in the formats of the scanner's locale. A scanner's
 * <a name="initial-locale">initial locale </a>is the value returned by the {@link
 * java.util.Locale#getDefault(Locale.Category)
 * Locale.getDefault(Locale.Category.FORMAT)} method; it may be changed via the {@link
 * #useLocale} method. The {@link #reset} method will reset the value of the
 * scanner's locale to the initial locale regardless of whether it was
 * previously changed.
 *
 * <p>The localized formats are defined in terms of the following parameters,
 * which for a particular locale are taken from that locale's {@link
 * java.text.DecimalFormat DecimalFormat} object, <tt>df</tt>, and its and
 * {@link java.text.DecimalFormatSymbols DecimalFormatSymbols} object,
 * <tt>dfs</tt>.
 *
 * <blockquote><dl>
 *     <dt><i>LocalGroupSeparator&nbsp;&nbsp;</i>
 *         <dd>The character used to separate thousands groups,
 *         <i>i.e.,</i>&nbsp;<tt>dfs.</tt>{@link
 *         java.text.DecimalFormatSymbols#getGroupingSeparator
 *         getGroupingSeparator()}
 *     <dt><i>LocalDecimalSeparator&nbsp;&nbsp;</i>
 *         <dd>The character used for the decimal point,
 *     <i>i.e.,</i>&nbsp;<tt>dfs.</tt>{@link
 *     java.text.DecimalFormatSymbols#getDecimalSeparator
 *     getDecimalSeparator()}
 *     <dt><i>LocalPositivePrefix&nbsp;&nbsp;</i>
 *         <dd>The string that appears before a positive number (may
 *         be empty), <i>i.e.,</i>&nbsp;<tt>df.</tt>{@link
 *         java.text.DecimalFormat#getPositivePrefix
 *         getPositivePrefix()}
 *     <dt><i>LocalPositiveSuffix&nbsp;&nbsp;</i>
 *         <dd>The string that appears after a positive number (may be
 *         empty), <i>i.e.,</i>&nbsp;<tt>df.</tt>{@link
 *         java.text.DecimalFormat#getPositiveSuffix
 *         getPositiveSuffix()}
 *     <dt><i>LocalNegativePrefix&nbsp;&nbsp;</i>
 *         <dd>The string that appears before a negative number (may
 *         be empty), <i>i.e.,</i>&nbsp;<tt>df.</tt>{@link
 *         java.text.DecimalFormat#getNegativePrefix
 *         getNegativePrefix()}
 *     <dt><i>LocalNegativeSuffix&nbsp;&nbsp;</i>
 *         <dd>The string that appears after a negative number (may be
 *         empty), <i>i.e.,</i>&nbsp;<tt>df.</tt>{@link
 *     java.text.DecimalFormat#getNegativeSuffix
 *     getNegativeSuffix()}
 *     <dt><i>LocalNaN&nbsp;&nbsp;</i>
 *         <dd>The string that represents not-a-number for
 *         floating-point values,
 *         <i>i.e.,</i>&nbsp;<tt>dfs.</tt>{@link
 *         java.text.DecimalFormatSymbols#getNaN
 *         getNaN()}
 *     <dt><i>LocalInfinity&nbsp;&nbsp;</i>
 *         <dd>The string that represents infinity for floating-point
 *         values, <i>i.e.,</i>&nbsp;<tt>dfs.</tt>{@link
 *         java.text.DecimalFormatSymbols#getInfinity
 *         getInfinity()}
 * </dl></blockquote>
 *
 * <h4> <a name="number-syntax">Number syntax</a> </h4>
 *
 * <p> The strings that can be parsed as numbers by an instance of this class
 * are specified in terms of the following regular-expression grammar, where
 * Rmax is the highest digit in the radix being used (for example, Rmax is 9 in base 10).
 *
 * <dl>
 *   <dt><i>NonAsciiDigit</i>:
 *       <dd>A non-ASCII character c for which
 *            {@link java.lang.Character#isDigit Character.isDigit}<tt>(c)</tt>
 *                        returns&nbsp;true
 *
 *   <dt><i>Non0Digit</i>:
 *       <dd><tt>[1-</tt><i>Rmax</i><tt>] | </tt><i>NonASCIIDigit</i>
 *
 *   <dt><i>Digit</i>:
 *       <dd><tt>[0-</tt><i>Rmax</i><tt>] | </tt><i>NonASCIIDigit</i>
 *
 *   <dt><i>GroupedNumeral</i>:
 *       <dd><tt>(&nbsp;</tt><i>Non0Digit</i>
 *                   <i>Digit</i><tt>?
 *                   </tt><i>Digit</i><tt>?</tt>
 *       <dd>&nbsp;&nbsp;&nbsp;&nbsp;<tt>(&nbsp;</tt><i>LocalGroupSeparator</i>
 *                         <i>Digit</i>
 *                         <i>Digit</i>
 *                         <i>Digit</i><tt> )+ )</tt>
 *
 *   <dt><i>Numeral</i>:
 *       <dd><tt>( ( </tt><i>Digit</i><tt>+ )
 *               | </tt><i>GroupedNumeral</i><tt> )</tt>
 *
 *   <dt><a name="Integer-regex"><i>Integer</i>:</a>
 *       <dd><tt>( [-+]? ( </tt><i>Numeral</i><tt>
 *                               ) )</tt>
 *       <dd><tt>| </tt><i>LocalPositivePrefix</i> <i>Numeral</i>
 *                      <i>LocalPositiveSuffix</i>
 *       <dd><tt>| </tt><i>LocalNegativePrefix</i> <i>Numeral</i>
 *                 <i>LocalNegativeSuffix</i>
 *
 *   <dt><i>DecimalNumeral</i>:
 *       <dd><i>Numeral</i>
 *       <dd><tt>| </tt><i>Numeral</i>
 *                 <i>LocalDecimalSeparator</i>
 *                 <i>Digit</i><tt>*</tt>
 *       <dd><tt>| </tt><i>LocalDecimalSeparator</i>
 *                 <i>Digit</i><tt>+</tt>
 *
 *   <dt><i>Exponent</i>:
 *       <dd><tt>( [eE] [+-]? </tt><i>Digit</i><tt>+ )</tt>
 *
 *   <dt><a name="Decimal-regex"><i>Decimal</i>:</a>
 *       <dd><tt>( [-+]? </tt><i>DecimalNumeral</i>
 *                         <i>Exponent</i><tt>? )</tt>
 *       <dd><tt>| </tt><i>LocalPositivePrefix</i>
 *                 <i>DecimalNumeral</i>
 *                 <i>LocalPositiveSuffix</i>
 *                 <i>Exponent</i><tt>?</tt>
 *       <dd><tt>| </tt><i>LocalNegativePrefix</i>
 *                 <i>DecimalNumeral</i>
 *                 <i>LocalNegativeSuffix</i>
 *                 <i>Exponent</i><tt>?</tt>
 *
 *   <dt><i>HexFloat</i>:
 *       <dd><tt>[-+]? 0[xX][0-9a-fA-F]*\.[0-9a-fA-F]+
 *                 ([pP][-+]?[0-9]+)?</tt>
 *
 *   <dt><i>NonNumber</i>:
 *       <dd><tt>NaN
 *                          | </tt><i>LocalNan</i><tt>
 *                          | Infinity
 *                          | </tt><i>LocalInfinity</i>
 *
 *   <dt><i>SignedNonNumber</i>:
 *       <dd><tt>( [-+]? </tt><i>NonNumber</i><tt> )</tt>
 *       <dd><tt>| </tt><i>LocalPositivePrefix</i>
 *                 <i>NonNumber</i>
 *                 <i>LocalPositiveSuffix</i>
 *       <dd><tt>| </tt><i>LocalNegativePrefix</i>
 *                 <i>NonNumber</i>
 *                 <i>LocalNegativeSuffix</i>
 *
 *   <dt><a name="Float-regex"><i>Float</i></a>:
 *       <dd><i>Decimal</i>
 *           <tt>| </tt><i>HexFloat</i>
 *           <tt>| </tt><i>SignedNonNumber</i>
 *
 * </dl>
 * <p>Whitespace is not significant in the above regular expressions.
 *
 * @since   1.5
 */
public final class Scanner implements Iterator<String>, Closeable {
   private static java.util.Scanner systemIn;
   private java.util.Scanner internal;

    // Constructors

    /**
     * Constructs a new <code>Scanner</code> that produces values scanned
     * from the specified source.
     *
     * @param  source A character source implementing the {@link Readable}
     *         interface
     */
    public Scanner(Readable source) {
       System.err.println("Readable " + source.equals(System.in));
       this.internal = new java.util.Scanner(source);
    }

    /**
     * Constructs a new <code>Scanner</code> that produces values scanned
     * from the specified input stream. Bytes from the stream are converted
     * into characters using the underlying platform's
     * {@linkplain java.nio.charset.Charset#defaultCharset() default charset}.
     *
     * @param  source An input stream to be scanned
     */
    public Scanner(InputStream source) {
       if (source.equals(System.in) && Scanner.systemIn != null) {
          this.internal = Scanner.systemIn;
       } else if (source.equals(System.in) && Scanner.systemIn == null) {
          this.internal = new java.util.Scanner(source);
          Scanner.systemIn = this.internal;
       } else {
          this.internal = new java.util.Scanner(source);
       }
    }

    /**
     * Constructs a new <code>Scanner</code> that produces values scanned
     * from the specified input stream. Bytes from the stream are converted
     * into characters using the specified charset.
     *
     * @param  source An input stream to be scanned
     * @param charsetName The encoding type used to convert bytes from the
     *        stream into characters to be scanned
     * @throws IllegalArgumentException if the specified character set
     *         does not exist
     */
    public Scanner(InputStream source, String charsetName) {
       System.err.println("InputStream, charsetName " +  source.equals(System.in));
       this.internal = new java.util.Scanner(source, charsetName);
    }

    /**
     * Constructs a new <code>Scanner</code> that produces values scanned
     * from the specified file. Bytes from the file are converted into
     * characters using the underlying platform's
     * {@linkplain java.nio.charset.Charset#defaultCharset() default charset}.
     *
     * @param  source A file to be scanned
     * @throws FileNotFoundException if source is not found
     */
    public Scanner(File source) throws FileNotFoundException {
       System.err.println("File " +  source.equals(System.in));
       this.internal = new java.util.Scanner(source);
    }

    /**
     * Constructs a new <code>Scanner</code> that produces values scanned
     * from the specified file. Bytes from the file are converted into
     * characters using the specified charset.
     *
     * @param  source A file to be scanned
     * @param charsetName The encoding type used to convert bytes from the file
     *        into characters to be scanned
     * @throws FileNotFoundException if source is not found
     * @throws IllegalArgumentException if the specified encoding is
     *         not found
     */
    public Scanner(File source, String charsetName)
        throws FileNotFoundException
    {
       System.err.println("File, charsetName " +  source.equals(System.in));
       this.internal = new java.util.Scanner(source, charsetName);
    }


    /**
     * Constructs a new <code>Scanner</code> that produces values scanned
     * from the specified file. Bytes from the file are converted into
     * characters using the underlying platform's
     * {@linkplain java.nio.charset.Charset#defaultCharset() default charset}.
     *
     * @param   source
     *          the path to the file to be scanned
     * @throws  IOException
     *          if an I/O error occurs opening source
     *
     * @since   1.7
     */
    public Scanner(Path source)
        throws IOException
    {
       System.err.println("Path " +  source.equals(System.in));
       this.internal = new java.util.Scanner(source);
    }

    /**
     * Constructs a new <code>Scanner</code> that produces values scanned
     * from the specified file. Bytes from the file are converted into
     * characters using the specified charset.
     *
     * @param   source
     *          the path to the file to be scanned
     * @param   charsetName
     *          The encoding type used to convert bytes from the file
     *          into characters to be scanned
     * @throws  IOException
     *          if an I/O error occurs opening source
     * @throws  IllegalArgumentException
     *          if the specified encoding is not found
     * @since   1.7
     */
    public Scanner(Path source, String charsetName) throws IOException {
       System.err.println("Path, charsetName " +  source.equals(System.in));
       this.internal = new java.util.Scanner(source, charsetName);
    }

    /**
     * Constructs a new <code>Scanner</code> that produces values scanned
     * from the specified string.
     *
     * @param  source A string to scan
     */
    public Scanner(String source) {
       System.err.println("String " +  source.equals(System.in));
       this.internal = new java.util.Scanner(source);
    }

    /**
     * Constructs a new <code>Scanner</code> that produces values scanned
     * from the specified channel. Bytes from the source are converted into
     * characters using the underlying platform's
     * {@linkplain java.nio.charset.Charset#defaultCharset() default charset}.
     *
     * @param  source A channel to scan
     */
    public Scanner(ReadableByteChannel source) {
       System.err.println("ReadableByteChannel " +  source.equals(System.in));
       this.internal = new java.util.Scanner(source);
    }

    /**
     * Constructs a new <code>Scanner</code> that produces values scanned
     * from the specified channel. Bytes from the source are converted into
     * characters using the specified charset.
     *
     * @param  source A channel to scan
     * @param charsetName The encoding type used to convert bytes from the
     *        channel into characters to be scanned
     * @throws IllegalArgumentException if the specified character set
     *         does not exist
     */
    public Scanner(ReadableByteChannel source, String charsetName) {
       System.err.println("ReadableByteChannel, charsetName " +  source.equals(System.in));
       this.internal = new java.util.Scanner(source, charsetName);
    }


    // Public methods

    /**
     * Closes this scanner.
     *
     * <p> If this scanner has not yet been closed then if its underlying
     * {@linkplain java.lang.Readable readable} also implements the {@link
     * java.io.Closeable} interface then the readable's <tt>close</tt> method
     * will be invoked.  If this scanner is already closed then invoking this
     * method will have no effect.
     *
     * <p>Attempting to perform search operations after a scanner has
     * been closed will result in an {@link IllegalStateException}.
     *
     */
    public void close() {
       this.internal.close();
    }

    /**
     * Returns the <code>IOException</code> last thrown by this
     * <code>Scanner</code>'s underlying <code>Readable</code>. This method
     * returns <code>null</code> if no such exception exists.
     *
     * @return the last exception thrown by this scanner's readable
     */
    public IOException ioException() {
       return this.internal.ioException();
    }

    /**
     * Returns the <code>Pattern</code> this <code>Scanner</code> is currently
     * using to match delimiters.
     *
     * @return this scanner's delimiting pattern.
     */
    public Pattern delimiter() {
       return this.internal.delimiter();
    }

    /**
     * Sets this scanner's delimiting pattern to the specified pattern.
     *
     * @param pattern A delimiting pattern
     * @return this scanner
     */
    public Scanner useDelimiter(Pattern pattern) {
       this.internal = this.internal.useDelimiter(pattern);
       return this;
    }

    /**
     * Sets this scanner's delimiting pattern to a pattern constructed from
     * the specified <code>String</code>.
     *
     * <p> An invocation of this method of the form
     * <tt>useDelimiter(pattern)</tt> behaves in exactly the same way as the
     * invocation <tt>useDelimiter(Pattern.compile(pattern))</tt>.
     *
     * <p> Invoking the {@link #reset} method will set the scanner's delimiter
     * to the <a href= "#default-delimiter">default</a>.
     *
     * @param pattern A string specifying a delimiting pattern
     * @return this scanner
     */
    public Scanner useDelimiter(String pattern) {
       this.internal = this.internal.useDelimiter(pattern);
       return this;
    }

    /**
     * Returns this scanner's locale.
     *
     * <p>A scanner's locale affects many elements of its default
     * primitive matching regular expressions; see
     * <a href= "#localized-numbers">localized numbers</a> above.
     *
     * @return this scanner's locale
     */
    public Locale locale() {
       return this.internal.locale();
    }

    /**
     * Sets this scanner's locale to the specified locale.
     *
     * <p>A scanner's locale affects many elements of its default
     * primitive matching regular expressions; see
     * <a href= "#localized-numbers">localized numbers</a> above.
     *
     * <p>Invoking the {@link #reset} method will set the scanner's locale to
     * the <a href= "#initial-locale">initial locale</a>.
     *
     * @param locale A string specifying the locale to use
     * @return this scanner
     */
    public Scanner useLocale(Locale locale) {
       this.internal = this.internal.useLocale(locale);
       return this;
    }

    /**
     * Returns this scanner's default radix.
     *
     * <p>A scanner's radix affects elements of its default
     * number matching regular expressions; see
     * <a href= "#localized-numbers">localized numbers</a> above.
     *
     * @return the default radix of this scanner
     */
    public int radix() {
       return this.internal.radix();
    }

    /**
     * Sets this scanner's default radix to the specified radix.
     *
     * <p>A scanner's radix affects elements of its default
     * number matching regular expressions; see
     * <a href= "#localized-numbers">localized numbers</a> above.
     *
     * <p>If the radix is less than <code>Character.MIN_RADIX</code>
     * or greater than <code>Character.MAX_RADIX</code>, then an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * <p>Invoking the {@link #reset} method will set the scanner's radix to
     * <code>10</code>.
     *
     * @param radix The radix to use when scanning numbers
     * @return this scanner
     * @throws IllegalArgumentException if radix is out of range
     */
    public Scanner useRadix(int radix) {
       this.internal.useRadix(radix);
       return this;
    }

    /**
     * Returns the match result of the last scanning operation performed
     * by this scanner. This method throws <code>IllegalStateException</code>
     * if no match has been performed, or if the last match was
     * not successful.
     *
     * <p>The various <code>next</code>methods of <code>Scanner</code>
     * make a match result available if they complete without throwing an
     * exception. For instance, after an invocation of the {@link #nextInt}
     * method that returned an int, this method returns a
     * <code>MatchResult</code> for the search of the
     * <a href="#Integer-regex"><i>Integer</i></a> regular expression
     * defined above. Similarly the {@link #findInLine},
     * {@link #findWithinHorizon}, and {@link #skip} methods will make a
     * match available if they succeed.
     *
     * @return a match result for the last match operation
     * @throws IllegalStateException  If no match result is available
     */
    public MatchResult match() {
       return this.internal.match();
    }

    /**
     * <p>Returns the string representation of this <code>Scanner</code>. The
     * string representation of a <code>Scanner</code> contains information
     * that may be useful for debugging. The exact format is unspecified.
     *
     * @return  The string representation of this scanner
     */
    public String toString() {
       return this.internal.toString();
    }

    /**
     * Returns true if this scanner has another token in its input.
     * This method may block while waiting for input to scan.
     * The scanner does not advance past any input.
     *
     * @return true if and only if this scanner has another token
     * @throws IllegalStateException if this scanner is closed
     * @see java.util.Iterator
     */
    public boolean hasNext() {
       return this.internal.hasNext();
    }

    /**
     * Finds and returns the next complete token from this scanner.
     * A complete token is preceded and followed by input that matches
     * the delimiter pattern. This method may block while waiting for input
     * to scan, even if a previous invocation of {@link #hasNext} returned
     * <code>true</code>.
     *
     * @return the next token
     * @throws NoSuchElementException if no more tokens are available
     * @throws IllegalStateException if this scanner is closed
     * @see java.util.Iterator
     */
    public String next() {
       return this.internal.next();
    }

    /**
     * The remove operation is not supported by this implementation of
     * <code>Iterator</code>.
     *
     * @throws UnsupportedOperationException if this method is invoked.
     * @see java.util.Iterator
     */
    public void remove() {
       this.internal.remove();
    }

    /**
     * Returns true if the next token matches the pattern constructed from the
     * specified string. The scanner does not advance past any input.
     *
     * <p> An invocation of this method of the form <tt>hasNext(pattern)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>hasNext(Pattern.compile(pattern))</tt>.
     *
     * @param pattern a string specifying the pattern to scan
     * @return true if and only if this scanner has another token matching
     *         the specified pattern
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNext(String pattern)  {
       return this.internal.hasNext(pattern);
    }

    /**
     * Returns the next token if it matches the pattern constructed from the
     * specified string.  If the match is successful, the scanner advances
     * past the input that matched the pattern.
     *
     * <p> An invocation of this method of the form <tt>next(pattern)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>next(Pattern.compile(pattern))</tt>.
     *
     * @param pattern a string specifying the pattern to scan
     * @return the next token
     * @throws NoSuchElementException if no such tokens are available
     * @throws IllegalStateException if this scanner is closed
     */
    public String next(String pattern)  {
       return this.internal.next(pattern);
    }

    /**
     * Returns true if the next complete token matches the specified pattern.
     * A complete token is prefixed and postfixed by input that matches
     * the delimiter pattern. This method may block while waiting for input.
     * The scanner does not advance past any input.
     *
     * @param pattern the pattern to scan for
     * @return true if and only if this scanner has another token matching
     *         the specified pattern
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNext(Pattern pattern) {
       return this.internal.hasNext(pattern);
    }

    /**
     * Returns the next token if it matches the specified pattern. This
     * method may block while waiting for input to scan, even if a previous
     * invocation of {@link #hasNext(Pattern)} returned <code>true</code>.
     * If the match is successful, the scanner advances past the input that
     * matched the pattern.
     *
     * @param pattern the pattern to scan for
     * @return the next token
     * @throws NoSuchElementException if no more tokens are available
     * @throws IllegalStateException if this scanner is closed
     */
    public String next(Pattern pattern) {
       return this.internal.next(pattern);
    }

    /**
     * Returns true if there is another line in the input of this scanner.
     * This method may block while waiting for input. The scanner does not
     * advance past any input.
     *
     * @return true if and only if this scanner has another line of input
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextLine() {
       return this.internal.hasNextLine();
    }

    /**
     * Advances this scanner past the current line and returns the input
     * that was skipped.
     *
     * This method returns the rest of the current line, excluding any line
     * separator at the end. The position is set to the beginning of the next
     * line.
     *
     * <p>Since this method continues to search through the input looking
     * for a line separator, it may buffer all of the input searching for
     * the line to skip if no line separators are present.
     *
     * @return the line that was skipped
     * @throws NoSuchElementException if no line was found
     * @throws IllegalStateException if this scanner is closed
     */
    public String nextLine() {
       return this.internal.nextLine();
    }

    // Public methods that ignore delimiters

    /**
     * Attempts to find the next occurrence of a pattern constructed from the
     * specified string, ignoring delimiters.
     *
     * <p>An invocation of this method of the form <tt>findInLine(pattern)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>findInLine(Pattern.compile(pattern))</tt>.
     *
     * @param pattern a string specifying the pattern to search for
     * @return the text that matched the specified pattern
     * @throws IllegalStateException if this scanner is closed
     */
    public String findInLine(String pattern) {
       return this.internal.findInLine(pattern);
    }

    /**
     * Attempts to find the next occurrence of the specified pattern ignoring
     * delimiters. If the pattern is found before the next line separator, the
     * scanner advances past the input that matched and returns the string that
     * matched the pattern.
     * If no such pattern is detected in the input up to the next line
     * separator, then <code>null</code> is returned and the scanner's
     * position is unchanged. This method may block waiting for input that
     * matches the pattern.
     *
     * <p>Since this method continues to search through the input looking
     * for the specified pattern, it may buffer all of the input searching for
     * the desired token if no line separators are present.
     *
     * @param pattern the pattern to scan for
     * @return the text that matched the specified pattern
     * @throws IllegalStateException if this scanner is closed
     */
    public String findInLine(Pattern pattern) {
       return this.internal.findInLine(pattern);
    }

    /**
     * Attempts to find the next occurrence of a pattern constructed from the
     * specified string, ignoring delimiters.
     *
     * <p>An invocation of this method of the form
     * <tt>findWithinHorizon(pattern)</tt> behaves in exactly the same way as
     * the invocation
     * <tt>findWithinHorizon(Pattern.compile(pattern, horizon))</tt>.
     *
     * @param pattern a string specifying the pattern to search for
     * @param horizon the search horizon
     * @return the text that matched the specified pattern
     * @throws IllegalStateException if this scanner is closed
     * @throws IllegalArgumentException if horizon is negative
     */
    public String findWithinHorizon(String pattern, int horizon) {
       return this.internal.findWithinHorizon(pattern, horizon);
    }

    /**
     * Attempts to find the next occurrence of the specified pattern.
     *
     * <p>This method searches through the input up to the specified
     * search horizon, ignoring delimiters. If the pattern is found the
     * scanner advances past the input that matched and returns the string
     * that matched the pattern. If no such pattern is detected then the
     * null is returned and the scanner's position remains unchanged. This
     * method may block waiting for input that matches the pattern.
     *
     * <p>A scanner will never search more than <code>horizon</code> code
     * points beyond its current position. Note that a match may be clipped
     * by the horizon; that is, an arbitrary match result may have been
     * different if the horizon had been larger. The scanner treats the
     * horizon as a transparent, non-anchoring bound (see {@link
     * Matcher#useTransparentBounds} and {@link Matcher#useAnchoringBounds}).
     *
     * <p>If horizon is <code>0</code>, then the horizon is ignored and
     * this method continues to search through the input looking for the
     * specified pattern without bound. In this case it may buffer all of
     * the input searching for the pattern.
     *
     * <p>If horizon is negative, then an IllegalArgumentException is
     * thrown.
     *
     * @param pattern the pattern to scan for
     * @param horizon the search horizon
     * @return the text that matched the specified pattern
     * @throws IllegalStateException if this scanner is closed
     * @throws IllegalArgumentException if horizon is negative
     */
    public String findWithinHorizon(Pattern pattern, int horizon) {
       return this.internal.findWithinHorizon(pattern, horizon);
    }

    /**
     * Skips input that matches the specified pattern, ignoring delimiters.
     * This method will skip input if an anchored match of the specified
     * pattern succeeds.
     *
     * <p>If a match to the specified pattern is not found at the
     * current position, then no input is skipped and a
     * <tt>NoSuchElementException</tt> is thrown.
     *
     * <p>Since this method seeks to match the specified pattern starting at
     * the scanner's current position, patterns that can match a lot of
     * input (".*", for example) may cause the scanner to buffer a large
     * amount of input.
     *
     * <p>Note that it is possible to skip something without risking a
     * <code>NoSuchElementException</code> by using a pattern that can
     * match nothing, e.g., <code>sc.skip("[ \t]*")</code>.
     *
     * @param pattern a string specifying the pattern to skip over
     * @return this scanner
     * @throws NoSuchElementException if the specified pattern is not found
     * @throws IllegalStateException if this scanner is closed
     */
    public Scanner skip(Pattern pattern) {
       this.internal = this.internal.skip(pattern);
       return this;
    }

    /**
     * Skips input that matches a pattern constructed from the specified
     * string.
     *
     * <p> An invocation of this method of the form <tt>skip(pattern)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>skip(Pattern.compile(pattern))</tt>.
     *
     * @param pattern a string specifying the pattern to skip over
     * @return this scanner
     * @throws IllegalStateException if this scanner is closed
     */
    public Scanner skip(String pattern) {
       this.internal = this.internal.skip(pattern);
       return this;
    }

    // Convenience methods for scanning primitives

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a boolean value using a case insensitive pattern
     * created from the string "true|false".  The scanner does not
     * advance past the input that matched.
     *
     * @return true if and only if this scanner's next token is a valid
     *         boolean value
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextBoolean()  {
        return this.internal.hasNextBoolean();
    }

    /**
     * Scans the next token of the input into a boolean value and returns
     * that value. This method will throw <code>InputMismatchException</code>
     * if the next token cannot be translated into a valid boolean value.
     * If the match is successful, the scanner advances past the input that
     * matched.
     *
     * @return the boolean scanned from the input
     * @throws InputMismatchException if the next token is not a valid boolean
     * @throws NoSuchElementException if input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean nextBoolean()  {
       return this.internal.nextBoolean();
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a byte value in the default radix using the
     * {@link #nextByte} method. The scanner does not advance past any input.
     *
     * @return true if and only if this scanner's next token is a valid
     *         byte value
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextByte() {
        return this.internal.hasNextByte();
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a byte value in the specified radix using the
     * {@link #nextByte} method. The scanner does not advance past any input.
     *
     * @param radix the radix used to interpret the token as a byte value
     * @return true if and only if this scanner's next token is a valid
     *         byte value
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextByte(int radix) {
       return this.internal.hasNextByte(radix);
    }

    /**
     * Scans the next token of the input as a <tt>byte</tt>.
     *
     * <p> An invocation of this method of the form
     * <tt>nextByte()</tt> behaves in exactly the same way as the
     * invocation <tt>nextByte(radix)</tt>, where <code>radix</code>
     * is the default radix of this scanner.
     *
     * @return the <tt>byte</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Integer</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public byte nextByte() {
         return this.internal.nextByte();
    }

    /**
     * Scans the next token of the input as a <tt>byte</tt>.
     * This method will throw <code>InputMismatchException</code>
     * if the next token cannot be translated into a valid byte value as
     * described below. If the translation is successful, the scanner advances
     * past the input that matched.
     *
     * <p> If the next token matches the <a
     * href="#Integer-regex"><i>Integer</i></a> regular expression defined
     * above then the token is converted into a <tt>byte</tt> value as if by
     * removing all locale specific prefixes, group separators, and locale
     * specific suffixes, then mapping non-ASCII digits into ASCII
     * digits via {@link Character#digit Character.digit}, prepending a
     * negative sign (-) if the locale specific negative prefixes and suffixes
     * were present, and passing the resulting string to
     * {@link Byte#parseByte(String, int) Byte.parseByte} with the
     * specified radix.
     *
     * @param radix the radix used to interpret the token as a byte value
     * @return the <tt>byte</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Integer</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public byte nextByte(int radix) {
       return this.internal.nextByte(radix);
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a short value in the default radix using the
     * {@link #nextShort} method. The scanner does not advance past any input.
     *
     * @return true if and only if this scanner's next token is a valid
     *         short value in the default radix
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextShort() {
        return this.internal.hasNextShort();
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a short value in the specified radix using the
     * {@link #nextShort} method. The scanner does not advance past any input.
     *
     * @param radix the radix used to interpret the token as a short value
     * @return true if and only if this scanner's next token is a valid
     *         short value in the specified radix
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextShort(int radix) {
       return this.internal.hasNextShort(radix);
    }

    /**
     * Scans the next token of the input as a <tt>short</tt>.
     *
     * <p> An invocation of this method of the form
     * <tt>nextShort()</tt> behaves in exactly the same way as the
     * invocation <tt>nextShort(radix)</tt>, where <code>radix</code>
     * is the default radix of this scanner.
     *
     * @return the <tt>short</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Integer</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public short nextShort() {
        return this.internal.nextShort();
    }

    /**
     * Scans the next token of the input as a <tt>short</tt>.
     * This method will throw <code>InputMismatchException</code>
     * if the next token cannot be translated into a valid short value as
     * described below. If the translation is successful, the scanner advances
     * past the input that matched.
     *
     * <p> If the next token matches the <a
     * href="#Integer-regex"><i>Integer</i></a> regular expression defined
     * above then the token is converted into a <tt>short</tt> value as if by
     * removing all locale specific prefixes, group separators, and locale
     * specific suffixes, then mapping non-ASCII digits into ASCII
     * digits via {@link Character#digit Character.digit}, prepending a
     * negative sign (-) if the locale specific negative prefixes and suffixes
     * were present, and passing the resulting string to
     * {@link Short#parseShort(String, int) Short.parseShort} with the
     * specified radix.
     *
     * @param radix the radix used to interpret the token as a short value
     * @return the <tt>short</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Integer</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public short nextShort(int radix) {
        return this.internal.nextShort(radix);
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as an int value in the default radix using the
     * {@link #nextInt} method. The scanner does not advance past any input.
     *
     * @return true if and only if this scanner's next token is a valid
     *         int value
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextInt() {
        return this.internal.hasNextInt();
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as an int value in the specified radix using the
     * {@link #nextInt} method. The scanner does not advance past any input.
     *
     * @param radix the radix used to interpret the token as an int value
     * @return true if and only if this scanner's next token is a valid
     *         int value
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextInt(int radix) {
       return this.internal.hasNextInt(radix);
    }

    /**
     * Scans the next token of the input as an <tt>int</tt>.
     *
     * <p> An invocation of this method of the form
     * <tt>nextInt()</tt> behaves in exactly the same way as the
     * invocation <tt>nextInt(radix)</tt>, where <code>radix</code>
     * is the default radix of this scanner.
     *
     * @return the <tt>int</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Integer</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public int nextInt() {
        return this.internal.nextInt();
    }

    /**
     * Scans the next token of the input as an <tt>int</tt>.
     * This method will throw <code>InputMismatchException</code>
     * if the next token cannot be translated into a valid int value as
     * described below. If the translation is successful, the scanner advances
     * past the input that matched.
     *
     * <p> If the next token matches the <a
     * href="#Integer-regex"><i>Integer</i></a> regular expression defined
     * above then the token is converted into an <tt>int</tt> value as if by
     * removing all locale specific prefixes, group separators, and locale
     * specific suffixes, then mapping non-ASCII digits into ASCII
     * digits via {@link Character#digit Character.digit}, prepending a
     * negative sign (-) if the locale specific negative prefixes and suffixes
     * were present, and passing the resulting string to
     * {@link Integer#parseInt(String, int) Integer.parseInt} with the
     * specified radix.
     *
     * @param radix the radix used to interpret the token as an int value
     * @return the <tt>int</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Integer</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public int nextInt(int radix) {
        return this.internal.nextInt(radix);
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a long value in the default radix using the
     * {@link #nextLong} method. The scanner does not advance past any input.
     *
     * @return true if and only if this scanner's next token is a valid
     *         long value
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextLong() {
       return this.internal.hasNextLong();
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a long value in the specified radix using the
     * {@link #nextLong} method. The scanner does not advance past any input.
     *
     * @param radix the radix used to interpret the token as a long value
     * @return true if and only if this scanner's next token is a valid
     *         long value
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextLong(int radix) {
       return this.internal.hasNextLong(radix);
    }

    /**
     * Scans the next token of the input as a <tt>long</tt>.
     *
     * <p> An invocation of this method of the form
     * <tt>nextLong()</tt> behaves in exactly the same way as the
     * invocation <tt>nextLong(radix)</tt>, where <code>radix</code>
     * is the default radix of this scanner.
     *
     * @return the <tt>long</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Integer</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public long nextLong() {
        return this.internal.nextLong();
    }

    /**
     * Scans the next token of the input as a <tt>long</tt>.
     * This method will throw <code>InputMismatchException</code>
     * if the next token cannot be translated into a valid long value as
     * described below. If the translation is successful, the scanner advances
     * past the input that matched.
     *
     * <p> If the next token matches the <a
     * href="#Integer-regex"><i>Integer</i></a> regular expression defined
     * above then the token is converted into a <tt>long</tt> value as if by
     * removing all locale specific prefixes, group separators, and locale
     * specific suffixes, then mapping non-ASCII digits into ASCII
     * digits via {@link Character#digit Character.digit}, prepending a
     * negative sign (-) if the locale specific negative prefixes and suffixes
     * were present, and passing the resulting string to
     * {@link Long#parseLong(String, int) Long.parseLong} with the
     * specified radix.
     *
     * @param radix the radix used to interpret the token as an int value
     * @return the <tt>long</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Integer</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public long nextLong(int radix) {
       return this.internal.nextLong(radix);
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a float value using the {@link #nextFloat}
     * method. The scanner does not advance past any input.
     *
     * @return true if and only if this scanner's next token is a valid
     *         float value
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextFloat() {
       return this.internal.hasNextFloat();
    }

    /**
     * Scans the next token of the input as a <tt>float</tt>.
     * This method will throw <code>InputMismatchException</code>
     * if the next token cannot be translated into a valid float value as
     * described below. If the translation is successful, the scanner advances
     * past the input that matched.
     *
     * <p> If the next token matches the <a
     * href="#Float-regex"><i>Float</i></a> regular expression defined above
     * then the token is converted into a <tt>float</tt> value as if by
     * removing all locale specific prefixes, group separators, and locale
     * specific suffixes, then mapping non-ASCII digits into ASCII
     * digits via {@link Character#digit Character.digit}, prepending a
     * negative sign (-) if the locale specific negative prefixes and suffixes
     * were present, and passing the resulting string to
     * {@link Float#parseFloat Float.parseFloat}. If the token matches
     * the localized NaN or infinity strings, then either "Nan" or "Infinity"
     * is passed to {@link Float#parseFloat(String) Float.parseFloat} as
     * appropriate.
     *
     * @return the <tt>float</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Float</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public float nextFloat() {
       return this.internal.nextFloat();
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a double value using the {@link #nextDouble}
     * method. The scanner does not advance past any input.
     *
     * @return true if and only if this scanner's next token is a valid
     *         double value
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextDouble() {
       return this.internal.hasNextDouble();
    }

    /**
     * Scans the next token of the input as a <tt>double</tt>.
     * This method will throw <code>InputMismatchException</code>
     * if the next token cannot be translated into a valid double value.
     * If the translation is successful, the scanner advances past the input
     * that matched.
     *
     * <p> If the next token matches the <a
     * href="#Float-regex"><i>Float</i></a> regular expression defined above
     * then the token is converted into a <tt>double</tt> value as if by
     * removing all locale specific prefixes, group separators, and locale
     * specific suffixes, then mapping non-ASCII digits into ASCII
     * digits via {@link Character#digit Character.digit}, prepending a
     * negative sign (-) if the locale specific negative prefixes and suffixes
     * were present, and passing the resulting string to
     * {@link Double#parseDouble Double.parseDouble}. If the token matches
     * the localized NaN or infinity strings, then either "Nan" or "Infinity"
     * is passed to {@link Double#parseDouble(String) Double.parseDouble} as
     * appropriate.
     *
     * @return the <tt>double</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Float</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if the input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public double nextDouble() {
       return this.internal.nextDouble();
    }

    // Convenience methods for scanning multi precision numbers

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a <code>BigInteger</code> in the default radix using the
     * {@link #nextBigInteger} method. The scanner does not advance past any
     * input.
     *
     * @return true if and only if this scanner's next token is a valid
     *         <code>BigInteger</code>
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextBigInteger() {
        return this.internal.hasNextBigInteger();
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a <code>BigInteger</code> in the specified radix using
     * the {@link #nextBigInteger} method. The scanner does not advance past
     * any input.
     *
     * @param radix the radix used to interpret the token as an integer
     * @return true if and only if this scanner's next token is a valid
     *         <code>BigInteger</code>
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextBigInteger(int radix) {
       return this.internal.hasNextBigInteger(radix);
    }

    /**
     * Scans the next token of the input as a {@link java.math.BigInteger
     * BigInteger}.
     *
     * <p> An invocation of this method of the form
     * <tt>nextBigInteger()</tt> behaves in exactly the same way as the
     * invocation <tt>nextBigInteger(radix)</tt>, where <code>radix</code>
     * is the default radix of this scanner.
     *
     * @return the <tt>BigInteger</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Integer</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if the input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public BigInteger nextBigInteger() {
        return this.internal.nextBigInteger();
    }

    /**
     * Scans the next token of the input as a {@link java.math.BigInteger
     * BigInteger}.
     *
     * <p> If the next token matches the <a
     * href="#Integer-regex"><i>Integer</i></a> regular expression defined
     * above then the token is converted into a <tt>BigInteger</tt> value as if
     * by removing all group separators, mapping non-ASCII digits into ASCII
     * digits via the {@link Character#digit Character.digit}, and passing the
     * resulting string to the {@link
     * java.math.BigInteger#BigInteger(java.lang.String)
     * BigInteger(String, int)} constructor with the specified radix.
     *
     * @param radix the radix used to interpret the token
     * @return the <tt>BigInteger</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Integer</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if the input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public BigInteger nextBigInteger(int radix) {
       return this.internal.nextBigInteger(radix);
    }

    /**
     * Returns true if the next token in this scanner's input can be
     * interpreted as a <code>BigDecimal</code> using the
     * {@link #nextBigDecimal} method. The scanner does not advance past any
     * input.
     *
     * @return true if and only if this scanner's next token is a valid
     *         <code>BigDecimal</code>
     * @throws IllegalStateException if this scanner is closed
     */
    public boolean hasNextBigDecimal() {
       return this.internal.hasNextBigDecimal();
    }

    /**
     * Scans the next token of the input as a {@link java.math.BigDecimal
     * BigDecimal}.
     *
     * <p> If the next token matches the <a
     * href="#Decimal-regex"><i>Decimal</i></a> regular expression defined
     * above then the token is converted into a <tt>BigDecimal</tt> value as if
     * by removing all group separators, mapping non-ASCII digits into ASCII
     * digits via the {@link Character#digit Character.digit}, and passing the
     * resulting string to the {@link
     * java.math.BigDecimal#BigDecimal(java.lang.String) BigDecimal(String)}
     * constructor.
     *
     * @return the <tt>BigDecimal</tt> scanned from the input
     * @throws InputMismatchException
     *         if the next token does not match the <i>Decimal</i>
     *         regular expression, or is out of range
     * @throws NoSuchElementException if the input is exhausted
     * @throws IllegalStateException if this scanner is closed
     */
    public BigDecimal nextBigDecimal() {
       return this.internal.nextBigDecimal();
    }

    /**
     * Resets this scanner.
     *
     * <p> Resetting a scanner discards all of its explicit state
     * information which may have been changed by invocations of {@link
     * #useDelimiter}, {@link #useLocale}, or {@link #useRadix}.
     *
     * <p> An invocation of this method of the form
     * <tt>scanner.reset()</tt> behaves in exactly the same way as the
     * invocation
     *
     * <blockquote><pre>{@code
     *   scanner.useDelimiter("\\p{javaWhitespace}+")
     *          .useLocale(Locale.getDefault(Locale.Category.FORMAT))
     *          .useRadix(10);
     * }</pre></blockquote>
     *
     * @return this scanner
     *
     * @since 1.6
     */
    public Scanner reset() {
       this.internal = this.internal.reset();
       return this;
    }

   public static void resetSystemIn() {
      Scanner.systemIn = null;
   }
}
