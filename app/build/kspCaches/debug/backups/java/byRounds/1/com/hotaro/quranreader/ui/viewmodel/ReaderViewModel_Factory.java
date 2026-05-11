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
public final class ReaderViewModel_Factory implements Factory<ReaderViewModel> {
  private final Provider<QuranRepository> repositoryProvider;

  public ReaderViewModel_Factory(Provider<QuranRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ReaderViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ReaderViewModel_Factory create(Provider<QuranRepository> repositoryProvider) {
    return new ReaderViewModel_Factory(repositoryProvider);
  }

  public static ReaderViewModel newInstance(QuranRepository repository) {
    return new ReaderViewModel(repository);
  }
}
