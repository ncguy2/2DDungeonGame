package net.ncguy.viewport;

import java.util.Optional;
import java.util.Stack;

public class FBOStack {
    
    protected static Stack<StackableFBO> stack = new Stack<>();

    /**
     * @return Optional containing the current framebuffer
     */
    public static Optional<StackableFBO> Current() {
        if(stack.isEmpty())
            return Optional.empty();
        return Optional.of(stack.peek());
    }

    /**
     * @return The added framebuffer
     */
    public static StackableFBO Push(StackableFBO fbo) {
        Current().ifPresent(StackableFBO::EndFBO);
        fbo.BeginFBO();
        return stack.push(fbo);
    }

    /**
     * @return The removed framebuffer
     */
    public static Optional<StackableFBO> Pop() {
        if(stack.isEmpty())
            return Optional.empty();

        StackableFBO fbo = stack.pop();
        fbo.EndFBO();


        Current().ifPresent(StackableFBO::BeginFBO);
        return Optional.of(fbo);
    }

}
