package com.hotaro.quranreader.data.local;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class PreferenceManager_Factory implements Factory<PreferenceManager> {
  private final Provider<Context> contextProvider;

  public PreferenceManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PreferenceManager get() {
    return newInstance(contextProvider.get());
  }

  public static PreferenceManager_Factory create(Provider<Context> contextProvider) {
    return new PreferenceManager_Factory(contextProvider);
  }

  public static PreferenceManager newInstance(Context context) {
    return new PreferenceManager(context);
  }
}
