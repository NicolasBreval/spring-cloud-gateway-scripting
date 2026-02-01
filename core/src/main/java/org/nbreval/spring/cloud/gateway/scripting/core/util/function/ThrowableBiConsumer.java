package org.nbreval.spring.cloud.gateway.scripting.core.util.function;

/** Custom consumer with two input arguments wich allows to throw a concrete exception. */
@FunctionalInterface
public interface ThrowableBiConsumer<A, B, E extends Exception> {
  void consume(A a, B b) throws E;
}
