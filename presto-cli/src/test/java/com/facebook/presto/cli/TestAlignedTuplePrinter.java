package com.facebook.presto.cli;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.io.StringWriter;
import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

public class TestAlignedTuplePrinter
{
    @Test
    public void testAlignedPrinting()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last", "quantity");
        OutputPrinter printer = new AlignedTuplePrinter(fieldNames, writer);

        printer.printRows(rows(
                row("hello", "world", 123),
                row("a", null, 4.5),
                row("some long\ntext that\ndoes not\nfit on\none line", "more\ntext", 4567),
                row("bye", "done", -15)),
                true);
        printer.finish();

        String expected = "" +
                "   first   | last  | quantity \n" +
                "-----------+-------+----------\n" +
                " hello     | world |      123 \n" +
                " a         | NULL  |      4.5 \n" +
                " some long+| more +|     4567 \n" +
                " text that+| text  |          \n" +
                " does not +|       |          \n" +
                " fit on   +|       |          \n" +
                " one line  |       |          \n" +
                " bye       | done  |      -15 \n" +
                "(4 rows)\n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testAlignedPrintingOneRow()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last");
        OutputPrinter printer = new AlignedTuplePrinter(fieldNames, writer);

        printer.printRows(rows(row("a long line\nwithout wrapping", "text")), true);
        printer.finish();

        String expected = "" +
                "      first       | last \n" +
                "------------------+------\n" +
                " a long line      | text \n" +
                " without wrapping |      \n" +
                "(1 row)\n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testAlignedPrintingNoRows()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last");
        OutputPrinter printer = new AlignedTuplePrinter(fieldNames, writer);

        printer.finish();

        String expected = "" +
                " first | last \n" +
                "-------+------\n" +
                "(0 rows)\n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    static List<?> row(Object... values)
    {
        return asList(values);
    }

    static List<List<?>> rows(List<?>... rows)
    {
        return asList(rows);
    }
}