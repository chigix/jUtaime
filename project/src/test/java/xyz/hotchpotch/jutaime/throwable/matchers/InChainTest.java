package xyz.hotchpotch.jutaime.throwable.matchers;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import xyz.hotchpotch.jutaime.throwable.Testee;

public class InChainTest {
    
    // [static members] ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    private static class TestMatcher extends TypeSafeMatcher<Throwable> {
        @Override
        protected boolean matchesSafely(Throwable t) {
            return false;
        }
        
        @Override
        public void describeTo(Description description) {
            description.appendText("I'm TestMatcher.");
        }
    }
    
    // [instance members] ++++++++++++++++++++++++++++++++++++++++++++++++++++++
    
    @Test
    public void testInChain1() {
        // インスタンス化の検査
        assertThat(InChain.inChain(Throwable.class), instanceOf(InChain.class));
        assertThat(InChain.inChain(Exception.class, "message"), instanceOf(InChain.class));
        assertThat(InChain.inChain(RuntimeException.class, null), instanceOf(InChain.class));
        assertThat(InChain.inChain("message"), instanceOf(InChain.class));
        assertThat(InChain.inChain((String) null), instanceOf(InChain.class));
        assertThat(InChain.inChain(new TestMatcher()), instanceOf(InChain.class));
    }
    
    @Test(expected  = NullPointerException.class)
    public void testInChain2() {
        InChain.inChain((Class<? extends Throwable>) null);
    }
    
    @Test(expected  = NullPointerException.class)
    public void testInChain3() {
        InChain.inChain(null, "message");
    }
    
    @Test(expected  = NullPointerException.class)
    public void testInChain4() {
        InChain.inChain((Matcher<Throwable>) null);
    }
    
    @Test
    public void testMatchesSafely1() {
        // サブクラスも合格と判定する。
        assertThat(Testee.of(() -> { throw new Exception(); }), not(InChain.inChain(RuntimeException.class)));
        assertThat(Testee.of(() -> { throw new RuntimeException(); }), InChain.inChain(RuntimeException.class));
        assertThat(Testee.of(() -> { throw new NullPointerException(); }), InChain.inChain(RuntimeException.class));
    }
    
    @Test
    public void testMatchesSafely2() {
        // 型は考慮せずメッセージのみに基づいて判定する
        assertThat(Testee.of(() -> { throw new Throwable("msg"); }), InChain.inChain("msg"));
        assertThat(Testee.of(() -> { throw new Error("msg"); }), InChain.inChain("msg"));
        assertThat(Testee.of(() -> { throw new Exception("msg"); }), InChain.inChain("msg"));
        assertThat(Testee.of(() -> { throw new RuntimeException("msg"); }), InChain.inChain("msg"));
        
        assertThat(Testee.of(() -> { throw new Throwable((String) null); }), InChain.inChain((String) null));
        
        assertThat(Testee.of(() -> { throw new Throwable("diff"); }), not(InChain.inChain("msg")));
    }
}
