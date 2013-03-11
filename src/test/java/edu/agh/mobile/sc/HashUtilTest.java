package edu.agh.mobile.sc;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Przemyslaw Dadel
 */
public class HashUtilTest {

    @Test
    public void testHashString() throws Exception {
        final String tested = "tested";
        final String hash = HashUtil.hash(tested);
        Assert.assertNotNull(hash);
    }

    @Test
    public void testHashInt() throws Exception {
        final int tested = 13;
        final String hash = HashUtil.hash(13);
        Assert.assertNotNull(hash);
    }

    @Test
    public void testRepetiviteHash() throws Exception {
        final String tested = "tested";
        final String hash1 = HashUtil.hash(tested);
        final String hash2 = HashUtil.hash(tested);
        Assert.assertEquals(hash1, hash2);
    }
}
