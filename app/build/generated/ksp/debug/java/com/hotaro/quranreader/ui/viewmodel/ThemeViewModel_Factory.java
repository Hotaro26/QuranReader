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
public final class ThemeViewModel_Factory implements Factory<ThemeViewModel> {
  private final Provider<QuranRepository> repositoryProvider;

  public ThemeViewModel_Factory(Provider<QuranRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ThemeViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ThemeViewModel_Factory create(Provider<QuranRepository> repositoryProvider) {
    return new ThemeViewModel_Factory(repositoryProvider);
  }

  public static ThemeViewModel newInstance(QuranRepository repository) {
    return new ThemeViewModel(repository);
  }
}
