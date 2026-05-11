package com.hotaro.quranreader.ui.viewmodel;

import com.hotaro.quranreader.data.repository.QuranRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class SurahListViewModel_Factory implements Factory<SurahListViewModel> {
  private final Provider<QuranRepository> repositoryProvider;

  public SurahListViewModel_Factory(Provider<QuranRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SurahListViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static SurahListViewModel_Factory create(Provider<QuranRepository> repositoryProvider) {
    return new SurahListViewModel_Factory(repositoryProvider);
  }

  public static SurahListViewModel newInstance(QuranRepository repository) {
    return new SurahListViewModel(repository);
  }
}
