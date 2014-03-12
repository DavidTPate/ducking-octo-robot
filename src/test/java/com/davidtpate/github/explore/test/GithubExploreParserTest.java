package com.davidtpate.github.explore.test;

import com.davidtpate.github.explore.GithubExploreParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

public class GithubExploreParserTest {
    @Test
    public void testBadFilename() {
        GithubExploreParser parser = new GithubExploreParser();
        try {
            System.out.println(parser.parse(""));
            // If we reach here, we've failed. It should throw an IllegalArgumentException
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Do nothing
        } catch (FileNotFoundException e) {
            // If we reach here, we've failed. It should throw an IllegalArgumentException
            Assert.fail();
        }
    }

    @Test
    public void testMissingFile() {
        GithubExploreParser parser = new GithubExploreParser();
        try {
            System.out.println(parser.parse("src/test/resources/Example-Fail.msg"));
            // If we reach here, we've failed. It should throw an IllegalArgumentException
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // If we reach here, we've failed. It should throw an IllegalArgumentException
            Assert.fail();
        } catch (FileNotFoundException e) {
            // Do nothing
        }
    }

    @Test
    public void testExampleMessage() {
        GithubExploreParser parser = new GithubExploreParser();
        try {
            System.out.println(parser.parse("src/test/resources/Example.msg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}
