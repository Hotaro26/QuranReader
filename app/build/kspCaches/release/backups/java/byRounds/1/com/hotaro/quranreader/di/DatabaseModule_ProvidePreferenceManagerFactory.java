package com.hotaro.quranreader.di;

import android.content.Context;
import com.hotaro.quranreader.data.local.PreferenceManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseModule_ProvidePreferenceManagerFactory implements Factory<PreferenceManager> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvidePreferenceManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public PreferenceManager get() {
    return providePreferenceManager(contextProvider.get());
  }

  public static DatabaseModule_ProvidePreferenceManagerFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvidePreferenceManagerFactory(contextProvider);
  }

  public static PreferenceManager providePreferenceManager(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePreferenceManager(context));
  }
}
