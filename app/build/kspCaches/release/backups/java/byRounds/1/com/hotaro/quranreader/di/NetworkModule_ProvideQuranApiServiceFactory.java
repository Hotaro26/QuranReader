package com.hotaro.quranreader.di;

import com.hotaro.quranreader.data.remote.QuranApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideQuranApiServiceFactory implements Factory<QuranApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideQuranApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public QuranApiService get() {
    return provideQuranApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideQuranApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideQuranApiServiceFactory(retrofitProvider);
  }

  public static QuranApiService provideQuranApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideQuranApiService(retrofit));
  }
}
