1. LiveData

```kotlin
    @MainThread
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        assertMainThread("observe");
        if (owner.getLifecycle().getCurrentState() == DESTROYED) {
            // ignore
            return;
        }
        LifecycleBoundObserver wrapper = new LifecycleBoundObserver(owner, observer);
        ObserverWrapper existing = mObservers.putIfAbsent(observer, wrapper);
        if (existing != null && !existing.isAttachedTo(owner)) {
            throw new IllegalArgumentException("Cannot add the same observer"
                    + " with different lifecycles");
        }
        if (existing != null) {
            return;
        }
        owner.getLifecycle().addObserver(wrapper);
    }
```

code

```kotlin
viewmodel.observe(lifecycleOwner,Observer {// blcok})

```
block 中的代码如果是单例的场景下，当 lifecycleOwner 所在的页面（无论是 Activity 还是 Fragment) 在重复打开的时候，
会导致不同的 lifecycleOwner 绑定相同的 observer 。

2. Thread Java&Android 实现不一致。
