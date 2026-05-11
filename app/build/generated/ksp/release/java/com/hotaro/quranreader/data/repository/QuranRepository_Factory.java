package com.hotaro.quranreader.data.repository;

import com.hotaro.quranreader.data.local.BookmarkDao;
import com.hotaro.quranreader.data.local.PreferenceManager;
import com.hotaro.quranreader.data.remote.QuranApiService;
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
public final class QuranRepository_Factory implements Factory<QuranRepository> {
  private final Provider<QuranApiService> apiServiceProvider;

  private final Provider<BookmarkDao> bookmarkDaoProvider;

  private final Provider<PreferenceManager> preferenceManagerProvider;

  public QuranRepository_Factory(Provider<QuranApiService> apiServiceProvider,
      Provider<BookmarkDao> bookmarkDaoProvider,
      Provider<PreferenceManager> preferenceManagerProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.bookmarkDaoProvider = bookmarkDaoProvider;
    this.preferenceManagerProvider = preferenceManagerProvider;
  }

  @Override
  public QuranRepository get() {
    return newInstance(apiServiceProvider.get(), bookmarkDaoProvider.get(), preferenceManagerProvider.get());
  }

  public static QuranRepository_Factory create(Provider<QuranApiService> apiServiceProvider,
      Provider<BookmarkDao> bookmarkDaoProvider,
      Provider<PreferenceManager> preferenceManagerProvider) {
    return new QuranRepository_Factory(apiServiceProvider, bookmarkDaoProvider, preferenceManagerProvider);
  }

  public static QuranRepository newInstance(QuranApiService apiService, BookmarkDao bookmarkDao,
      PreferenceManager preferenceManager) {
    return new QuranRepository(apiService, bookmarkDao, preferenceManager);
  }
}
