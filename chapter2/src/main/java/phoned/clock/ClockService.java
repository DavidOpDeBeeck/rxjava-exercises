package phoned.clock;

import rx.Observable;
import rx.Subscriber;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class ClockService {

    private static final int THREADS = 1;

    private final Clock clock;
    private final int tickInterval;
    private final ExecutorService executorService;

    private Observable<LocalDateTime> currentTime;

    public ClockService(Clock clock, int tickInterval) {
        this.clock = clock;
        this.tickInterval = tickInterval;
        this.executorService = Executors.newFixedThreadPool(THREADS);
    }

    public void init() {
        this.currentTime = Observable.<LocalDateTime>create(subscriber ->
                executorService.submit(() -> notifySubscriberEveryInterval(subscriber)))
                .share();
    }

    public Observable<LocalDateTime> getTime() {
        return currentTime;
    }

    private void notifySubscriberEveryInterval(Subscriber<? super LocalDateTime> subscriber) {
        while (!subscriber.isUnsubscribed()) {
            notifySubscriberAndSleep(subscriber);
        }
    }

    private void notifySubscriberAndSleep(Subscriber<? super LocalDateTime> subscriber) {
        try {
            notifySubscriber(subscriber);
            sleep(tickInterval);
        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private void notifySubscriber(Subscriber<? super LocalDateTime> subscriber) {
        subscriber.onNext(LocalDateTime.now(clock));
    }
}
