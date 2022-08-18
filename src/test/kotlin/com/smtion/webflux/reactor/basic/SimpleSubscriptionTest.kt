package com.smtion.webflux.reactor.basic

import io.kotest.core.spec.style.FunSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class SimpleSubscriptionTest : FunSpec({

    val publisher: Flux<Int> = Flux.range(1, 50)

    test("Simple subscription flow with StepVerifier") {
        StepVerifier.create(publisher)
            .expectSubscription()
            .thenRequest(10)
            .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .thenRequest(10)
            .expectNext(11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
            .thenRequest(10)
            .expectNext(21, 22, 23, 24, 25, 26, 27 , 28, 29 ,30)
            .thenRequest(10)
            .expectNext(31, 32, 33, 34, 35, 36, 37 , 38, 39 ,40)
            .thenRequest(10)
            .expectNext(41, 42, 43, 44, 45, 46, 47 , 48, 49 ,50)
            .verifyComplete()
    }

    test("Simple subscription flow with deprecated implementation") {
        @Suppress("DEPRECATION")
        publisher.subscribe(
            { println("Element: $it") },
            { err -> err.printStackTrace() },
            { println("All elements have been processed.") },
            { subscription ->
                for (i in 0..4) {
                    println("Request the next 10 elements.")
                    subscription.request(10)
                }
            }
        )
        withContext(Dispatchers.IO) {
            Thread.sleep(50)
        }
    }

    test("Simple subscription flow with Subscriber") {
        publisher.subscribe(
            object : BaseSubscriber<Int>() {
                private val demand = 10L
                private var counter = 10

                override fun hookOnSubscribe(subscription: Subscription) {
                    println("Request the next 10 elements.")
                    request(demand)
                }

                override fun hookOnNext(value: Int) {
                    println("Element is $value")
                    if (--counter == 0)  {
                        println("Request the next 10 elements.")
                        counter = 10
                        request(demand)
                    }
                }

                override fun hookOnComplete() = println("All elements have been processed.")
            }
        )
        withContext(Dispatchers.IO) {
            Thread.sleep(50)
        }
    }
})